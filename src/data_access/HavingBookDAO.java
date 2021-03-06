package data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import javabeans.Book;
import javabeans.LendingBook;

public class HavingBookDAO {

	ConnectionShelf connector;

	/**
	 * 購入書籍を所有書籍テーブルに加える。
	 * 同時に新規書籍の場合、書籍の詳細情報を書籍情報テーブルにも加える。
	 * 所有書籍テーブルへの追加が成功であればtrue
	 * 但し書籍情報テーブルの追加成功は問わない
	 * @param book
	 * @return boolean 成功->true
	 */
	public boolean addBook(Book book) {
		//SQLを設定
		//1:ISBN
		final String SQL =
				"INSERT INTO having_book(books_id,title,boughtdate,count,checkedout_date,is_lending) VALUES(NULL,?,date(now()),0,NULL,0)";

		connector = new ConnectionAdmin();

		try (Connection connection = connector.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL)) {

			statement.setString(1, book.getTitle());

			//アップデートできたかのチェック。1件の追加なので戻り値が１なら成功
			int isSuccess = statement.executeUpdate();
			if (isSuccess == 1) {
				//書籍情報テーブルに本の情報を書き加える
				//ただし、isbnが被っていた場合この処理は失敗する
				//失敗は仕様上問題ないため、
				//この処理の成功失敗に関わらず呼び出し元にはtrueを返す
				BookInfoDAO dao = new BookInfoDAO();
				dao.addBookInfo(book);

				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 *  所有書籍一覧を返す
	 * @return List <Book>
	 */
	public List<LendingBook> searchBook() {
		final String SQL =
				"SELECT books_id,title,count,checkedout_date,is_lending FROM having_book";
		connector = new ConnectionUser();

		try(Connection connection = connector.getConnection();
				Statement statement = connection.createStatement()){

			List<LendingBook> lists = new ArrayList<>();

			ResultSet rs = statement.executeQuery(SQL);
			while (rs.next()) {
				LendingBook book = new LendingBook();
				book.setBooksId	(rs.getInt("books_id"));
				book.setTitle	(rs.getString("title"));
				book.setCount	(rs.getInt("count"));
				book.setCheckedoutDate(rs.getDate("checkedout_date"));
				book.setCheckedOut(rs.getInt("is_lending") == 1);

				BookInfoDAO dao = new BookInfoDAO();
				dao.searchBookInfo(book);

				lists.add(book);
			}
			return lists;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * LendingBookDAOのlendBook()メソッドからのみ呼び出される。
	 * ここで投げられる例外はそこで処理する
	 * トランザクション処理を行っているためこの処理失敗で前項メソッドも失敗になる
	 * @param book
	 * @return boolean 成功->true
	 * @throws SQLException
	 * @throws NamingException
	 */
	public boolean lendBook(Book book) throws SQLException, NamingException {

		//SQLの設定
		//貸し出し回数を１回増やす
		//貸し出し中の項目を「貸し出し中(1)」にする
		//①books_id
		final String SQL = "UPDATE having_book SET count = count + 1,checkedout_date = date(now()),is_lending = 1 WHERE books_id = ?";
		connector = new ConnectionUser();

		try(Connection connection = connector.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL)) {

			statement.setInt(1, book.getBooksId());

			//アップデート処理。1件のみの処理なので戻り値が1なら成功
			int isSuccess = statement.executeUpdate();
			return isSuccess == 1;
		}
	}

	/**
	 * LendingBookDAOのreturnBook()メソッドからのみ呼び出される。
	 * ここで投げられる例外はそこで処理する
	 * トランザクション処理を行っているためこの処理失敗で前項メソッドも失敗になる
	 * @param book
	 * @return boolean 成功->true
	 * @throws SQLException
	 * @throws NamingException
	 */
	public boolean returnBook(LendingBook book) throws SQLException, NamingException {
		//SQLの設定
		//所有書籍の貸し出し中の項目を「非貸し出し中(0)」にする
		//①books_id
		final String SQL = "UPDATE having_book SET is_lending = 0 WHERE books_id = ?";
		connector = new ConnectionUser();

		try(Connection connection = connector.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL)) {

			statement.setInt(1, book.getBooksId());

			//アップデート処理。1件のみの処理なので戻り値が1なら成功
			int isSuccess = statement.executeUpdate();
			return isSuccess == 1;
		}
	}
}