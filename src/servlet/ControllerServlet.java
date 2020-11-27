package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javabeans.UserDTO;
import model.LoginLogic;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class ControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ControllerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());

		//■Get通信(トップ画面から/ログイン失敗画面から）********************************
		//◇ログイン画面にフォワード
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);

		//■Post通信（ログイン画面から）*************************************************
		//◇ログイン成功⇒データをセッションに保存後、ログイン成功画面にフォワード
		//◇ログイン失敗⇒ログイン画面にリダイレクト

		//リクエストパラメータの取得
		request.setCharacterEncoding("UTF-8");
		String name = request.getParameter("name");
		String password = request.getParameter("password");

		//ログイン処理の実行
				Login login = new Login(name, password);
				LoginLogic bo = new LoginLogic();
				UserDTO userDTO = bo.execute(login);


		//userDTOデータの有無によって処理を分岐
		if (userDTO != null) {//ログイン成功
			//データをセッションスコープに保存
			HttpSession session = request.getSession();
			session.setAttribute("userDTO", userDTO);

			//ログイン成功画面へフォワード
			RequestDispatcher dispatcher = request.getRequestDispatcher("/loginOK.jsp");
			dispatcher.forward(request, response);

		} else {//ログイン失敗
				//ログイン画面にリダイレクト
			response.sendRedirect("/YourShelf/LoginServlet");
		}
	}

}
