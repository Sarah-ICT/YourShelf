package servlet;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class UserNameCheckServlet
 */
@WebServlet("/UserNameCheckServlet")
public class UserNameCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserNameCheckServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String name = request.getParameter("name");
		HttpSession session = request.getSession();
		HashSet<String> userNameSet = (HashSet<String>) session.getAttribute("userNameSet");
		String message = "";
		if(userNameSet.add(name)) {
			message = "登録可能です";
			request.setAttribute("name", name);
		} else {
			message = "既に使われています";
		}

		request.setAttribute("message", message);


		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/addUser.jsp");
		dispatcher.forward(request, response);
	}
}
