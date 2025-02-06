package chapter6.dao;

import static chapter6.utils.CloseableUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.Message;
import chapter6.beans.UserComment;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class CommentDao {

	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public CommentDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public void insert(Connection connection, Message comment) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO comments ( ");
			sql.append("    text, ");
			sql.append("    user_id, ");
			sql.append("    message_id, ");
			sql.append("    created_date, ");
			sql.append("    updated_date ");
			sql.append(") VALUES ( ");
			sql.append("    ?, ");                  // text
			sql.append("    ?, "); 					// user_id
			sql.append("    ?, ");   				 // tmessage_id
			sql.append("    CURRENT_TIMESTAMP, ");  // created_date
			sql.append("    CURRENT_TIMESTAMP ");   // updated_date
			sql.append(")");

			ps = connection.prepareStatement(sql.toString());

			ps.setString(1, comment.getText());
			ps.setInt(2, comment.getUserId());
			ps.setInt(3, comment.getId());

			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}
	public List<UserComment> select(Connection connection) {
		//Connection connection
		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			sql.append("    comments.id as id , ");
			sql.append("    comments.text as text , ");
			sql.append("    comments.user_id as user_id , ");
			sql.append("    comments.message_id as message_id , ");
			sql.append("    users.account as account , ");
			sql.append("    users.name as name, ");
			sql.append("    comments.created_date as created_date ");
			sql.append("FROM comments ");
			sql.append("INNER JOIN users ");
			sql.append("ON comments.user_id = users.id ");



			ps = connection.prepareStatement(sql.toString());

			ResultSet rs = ps.executeQuery();

			List<UserComment> messages = toComment(rs);
			return messages;
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	private List<UserComment> toComment(ResultSet rs) throws SQLException {
		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		List<UserComment> messages = new ArrayList<UserComment>();
		try {
			while (rs.next()) {
				UserComment message = new UserComment();
				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setUserId(rs.getInt("user_id"));
				message.setMessageId(rs.getInt("message_id"));
				message.setAccount(rs.getString("account"));
				message.setName(rs.getString("name"));
				message.setCreatedDate(rs.getTimestamp("created_date"));
				messages.add(message);
			}
			return messages;
		} finally {
			close(rs);
		}
	}



}
