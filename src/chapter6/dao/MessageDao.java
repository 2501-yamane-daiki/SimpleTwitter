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
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class MessageDao {
	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public MessageDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public void insert(Connection connection, Message message) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO messages ( ");
			sql.append("    user_id, ");
			sql.append("    text, ");
			sql.append("    created_date, ");
			sql.append("    updated_date ");
			sql.append(") VALUES ( ");
			sql.append("    ?, ");                  // user_id
			sql.append("    ?, ");                  // text
			sql.append("    CURRENT_TIMESTAMP, ");  // created_date
			sql.append("    CURRENT_TIMESTAMP ");   // updated_date
			sql.append(")");

			ps = connection.prepareStatement(sql.toString());

			ps.setInt(1, message.getUserId());
			ps.setString(2, message.getText());

			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}
	public void delete(Connection connection, String deleteMessage) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM messages  WHERE id = ? ;" );

			ps = connection.prepareStatement(sql.toString());

			ps.setString(1, deleteMessage);

			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}
	//DBから編集するカラムを取得
	public  List<Message> edit(Connection connection, String editMessage) {
		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM messages  WHERE id = ? ;" );

			ps = connection.prepareStatement(sql.toString());

			ps.setString(1, editMessage);

			ResultSet rs = ps.executeQuery();
			List<Message> messages = toMessages(rs);
			//リストをメッセージサーバーへ
			return messages;
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}
	private List<Message> toMessages(ResultSet rs) throws SQLException {


		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());
		//取得したカラムをlistに入れる　public  List<Message> editに返す
		List<Message> messages = new ArrayList<Message>();
		try {
			while (rs.next()) {
				Message editMessage = new Message();
				editMessage.setId(rs.getInt("id"));
				editMessage.setUserId(rs.getInt("user_id"));
				editMessage.setText(rs.getString("text"));
				editMessage.setCreatedDate(rs.getTimestamp("created_date"));
				editMessage.setUpdatedDate(rs.getTimestamp("Updated_date"));
				messages.add(editMessage);
			}
			return messages;
		} finally {
			close(rs);
		}
	}
	public void update(Connection connection, Message editMessage) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();

			sql.append("UPDATE message SET ");
			sql.append("    text = ?, ");
			sql.append("    updated_date = CURRENT_TIMESTAMP ");
			sql.append("WHERE id = ?");
			ps = connection.prepareStatement(sql.toString());
			ps.setString(1, editMessage.getText());
			ps.setInt(2, editMessage.getId());

			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}
}
