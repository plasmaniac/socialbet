package no.gnet.edvd.database.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import no.gnet.edvd.Bet;
import no.gnet.edvd.Currency;
import no.gnet.edvd.DataStore;
import no.gnet.edvd.Event;
import no.gnet.edvd.EventType;
import no.gnet.edvd.GenericEvent;
import no.gnet.edvd.Option;
import no.gnet.edvd.Player;
import no.gnet.edvd.exception.StorageException;

import org.springframework.stereotype.Component;

@Component
public class MySQLDatastore implements DataStore {

	DataSource datasource;

	public MySQLDatastore() {
		System.out.println("MySQLDatastore created");
	}

	public MySQLDatastore(DataSource datasource) {
		this.datasource = datasource;
	}

	@Override
	public int placeBet(Bet bet) {
		String insertTableSQL = "INSERT INTO BET"
				+ "(option_id, player_id, amount, currency) VALUES"
				+ "(?,?,?,?)";
		Connection conn = getDbConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(insertTableSQL,
					Statement.RETURN_GENERATED_KEYS);
			System.out.println("inserting bet, bet.option.id=" + bet.option.id);
			System.out.println("inserting bet, bet.player.id=" + bet.player.id);
			System.out.println("inserting bet, bet.amount=" + bet.amount);
			System.out.println("inserting bet, bet.currency.id="
					+ bet.currency.id);
			preparedStatement.setInt(1, bet.option.id);
			preparedStatement.setInt(2, bet.player.id);
			preparedStatement.setDouble(3, bet.amount);
			preparedStatement.setInt(4, bet.currency.id);
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			rs.next();
			int auto_id = rs.getInt(1);
			return auto_id;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
	}

	private void closeGracefully(Connection conn,
			PreparedStatement preparedStatement) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	private Connection getDbConnection() {
		try {
			return datasource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new StorageException("Failed to get connection to datasource");
	}

	@Override
	public void updateBet(Bet bet) {

	}

	@Override
	public Bet getBet(Bet bet) {
		return getBet(bet.id);
	}

	private Bet getBet(int betid) {
		String selectSQL = "SELECT eventid,option_id,optionname,player_id,amount,currency,shortname,eventdesc,firstname,lastname,email from bets_expanded "
				+ "WHERE betid = ?";
		Bet bet = null;
		PreparedStatement preparedStatement = null;
		Connection conn = null;
		try {
			conn = getDbConnection();
			preparedStatement = conn.prepareStatement(selectSQL);
			preparedStatement.setInt(1, betid);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				int amount = rs.getInt("amount");
				int currency = rs.getInt("currency");
				int option_id = rs.getInt("option_id");
				int player_id = rs.getInt("player_id");
				int eventid = rs.getInt("eventid");
				String firstname = rs.getString("firstname");
				String email = rs.getString("email");
				String lastname = rs.getString("lastname");
				String shortname = rs.getString("shortname");
				String eventdesc = rs.getString("eventdesc");
				String optionname = rs.getString("optionname");
				GenericEvent event = new GenericEvent();
				event.id = eventid;
				event.description = eventdesc;
				event.name = shortname;
				Player player = new Player(firstname, lastname, email);
				player.id = player_id;
				Option option = new Option();
				option.id = option_id;
				option.name = optionname;
				option.event = event;
				bet = new Bet(event, option, amount,
						currency == 1 ? Currency.NOK : Currency.EURO, player,
						betid);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
		return bet;
	}

	@Override
	public List<Bet> getBetsForEvent(Event event) {
		return getBetsForEvent(event.id);
	}

	@Override
	public List<Bet> getBetsForEvent(int eventid) {
		ArrayList<Bet> bets = new ArrayList<Bet>();
		String selectSQL = "SELECT betid,option_id,optionname,player_id,amount,currency,shortname,eventdesc,firstname,lastname,email from bets_expanded "
				+ "WHERE eventid = ?";
		PreparedStatement preparedStatement = null;
		Connection conn = null;
		try {
			conn = getDbConnection();
			preparedStatement = conn.prepareStatement(selectSQL);
			preparedStatement.setInt(1, eventid);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				int amount = rs.getInt("amount");
				int currency = rs.getInt("currency");
				int option_id = rs.getInt("option_id");
				int player_id = rs.getInt("player_id");
				int betid = rs.getInt("betid");
				String firstname = rs.getString("firstname");
				String email = rs.getString("email");
				String lastname = rs.getString("lastname");
				String shortname = rs.getString("shortname");
				String eventdesc = rs.getString("eventdesc");
				String optionname = rs.getString("optionname");
				GenericEvent event = new GenericEvent();
				event.id = eventid;
				event.description = eventdesc;
				event.name = shortname;
				Player player = new Player(firstname, lastname, email);
				player.id = player_id;
				Option option = new Option();
				option.id = option_id;
				option.name = optionname;
				option.event = event;
				Bet bet = new Bet(event, option, amount,
						currency == 1 ? Currency.NOK : Currency.EURO, player,
						betid);
				bets.add(bet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
		return bets;
	}

	@Override
	public Event storeEvent(Event event) {
		Event insertedEvent = event;
		int event_id = _putEvent(event);
		insertedEvent.id = event_id;
		for (Iterator<Option> iterator = insertedEvent.getOptions().iterator(); iterator
				.hasNext();) {
			Option option = iterator.next();
			option.event = insertedEvent;
			int oid = storeOption(option);
			option.id = oid;
		}
		return insertedEvent;
	}

	private int _putEvent(Event event) {
		verifyEventValid(event);
		
		String insertTableSQL = "INSERT INTO Event"
				+ "(shortname, description, betDeadline, eventStartTime) VALUES" + "(?,?,?,?)";
		Connection conn = getDbConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(insertTableSQL,
					Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, event.name);
			preparedStatement.setString(2, event.description);
			preparedStatement.setTimestamp(3, new Timestamp(event.betDeadline.getMillis()));
			preparedStatement.setTimestamp(4, new Timestamp(event.eventStartTime.getMillis()));
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			rs.next();
			int auto_id = rs.getInt(1);
			return auto_id;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
	}

	private void verifyEventValid(Event event) {
		if (event.description == null || event.description.length() < 1
				|| event.name == null || event.name.length() < 1)
			throw new IllegalArgumentException(
					"Name or description cannot be null");
		if (event.betDeadline == null || event.eventStartTime == null)
			throw new IllegalArgumentException(
					"betDeadline or eventStartTime cannot be null");
	}

	private int storeOption(Option option) {

		String insertTableSQL = "INSERT INTO betoption"
				+ "(name, event_id) VALUES" + "(?,?)";
		Connection conn = getDbConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(insertTableSQL,
					Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, option.name);
			preparedStatement.setInt(2, option.event.id);

			preparedStatement.executeUpdate();

			ResultSet rs = preparedStatement.getGeneratedKeys();
			rs.next();
			int auto_id = rs.getInt(1);
			return auto_id;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}

	}

	@Override
	public void updateEvent(Event event) {
		// TODO Auto-generated method stub

	}

	public List<Event> listEvents() {
		ArrayList<Event> events = new ArrayList<Event>();
		String selectSQL = "SELECT id,shortname,description FROM event";
		PreparedStatement preparedStatement = null;
		Connection conn = null;
		try {
			conn = getDbConnection();
			preparedStatement = conn.prepareStatement(selectSQL);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				int eventid = rs.getInt("id");
				String shortname = rs.getString("shortname");
				String description = rs.getString("description");
				GenericEvent event = new GenericEvent();
				event.id = eventid;
				event.description = description;
				event.name = shortname;
				events.add(event);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
		updateWithOptions(events);
		return events;

	}

	private void updateWithOptions(ArrayList<Event> events) {
		for (Iterator<Event> iterator = events.iterator(); iterator.hasNext();) {
			GenericEvent event = (GenericEvent) iterator.next();
			event.setOptions(getOptionsForEvent(event.id));
		}

	}

	@Override
	public List<Option> getOptionsForEvent(int eventid) {
		ArrayList<Option> options = new ArrayList<Option>();
		String selectSQL = "select name, id from betoption where event_id=?";
		PreparedStatement preparedStatement = null;
		Connection conn = null;
		try {
			conn = getDbConnection();
			preparedStatement = conn.prepareStatement(selectSQL);
			preparedStatement.setInt(1, eventid);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				int optionid = rs.getInt("id");
				String name = rs.getString("name");
				Option option = new Option();
				option.id = optionid;
				option.name = name;
				options.add(option);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
		return options;
	}

	@Override
	public void insertCurrency(Currency currency) {
		String insertTableSQL = "INSERT INTO currency"
				+ "(currency_id,commonName) VALUES" + "(?,?)";
		Connection conn = getDbConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(insertTableSQL,
					Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, currency.id);
			preparedStatement.setString(2, currency.name);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
	}

	@Override
	public Player storePlayer(Player player) {
		Player storedplayer = player;
		String insertTableSQL = "INSERT INTO player"
				+ "(firstname, lastname,email) VALUES" + "(?,?,?)";
		Connection conn = getDbConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(insertTableSQL,
					Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, player.firstname);
			preparedStatement.setString(2, player.lastname);
			preparedStatement.setString(3, player.email);
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			rs.next();
			int auto_id = rs.getInt(1);
			storedplayer.id = auto_id;
			return storedplayer;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
	}

	@Override
	public boolean playerExists(Player player) {
		String selectSQL = "SELECT id from player WHERE email like ?";
		PreparedStatement preparedStatement = null;
		Connection conn = null;
		boolean match = true;
		try {
			conn = getDbConnection();
			preparedStatement = conn.prepareStatement(selectSQL);
			preparedStatement.setString(1, player.email);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.isBeforeFirst()) {
				match = false;
			}
			return match;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
	}

	@Override
	public Player getPlayer(Player player) {
		Player storedPlayer = null;
		String selectSQL = "SELECT id,firstname,lastname,email from player WHERE email like ?";
		PreparedStatement preparedStatement = null;
		Connection conn = null;
		try {
			conn = getDbConnection();
			preparedStatement = conn.prepareStatement(selectSQL);
			preparedStatement.setString(1, player.email);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				String firstname = rs.getString("firstname");
				String lastname = rs.getString("lastname");
				String email = rs.getString("email");
				int id = rs.getInt("id");
				storedPlayer = new Player(firstname, lastname, email);
				storedPlayer.id = id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
		return storedPlayer;
	}

	public void placeBets(Bet... bets) {
		for (int i = 0; i < bets.length; i++) {
			placeBet(bets[i]);
		}
	}

	@Override
	public boolean currenciesEstablished() {
		String selectSQL = "SELECT count(currency_id) as count from currency";
		PreparedStatement preparedStatement = null;
		Connection conn = null;
		try {
			conn = getDbConnection();
			preparedStatement = conn.prepareStatement(selectSQL);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
		return false;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	@Override
	public Event getEvent(int iid) {
		GenericEvent event = null;
		String selectSQL = "SELECT shortname,description from event WHERE id = ?";
		PreparedStatement preparedStatement = null;
		Connection conn = null;
		try {
			conn = getDbConnection();
			preparedStatement = conn.prepareStatement(selectSQL);
			preparedStatement.setInt(1, iid);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				String shortname = rs.getString("shortname");
				String description = rs.getString("description");
				event = new GenericEvent();
				event.id = iid;
				event.description = description;
				event.name = shortname;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
		event.setOptions(getOptionsForEvent(event.id));
		return event;
	}

	@Override
	public Option getOption(int optionid) {
		Option option = null;
		String selectSQL = "SELECT name,event_id from betoption WHERE id = ?";
		PreparedStatement preparedStatement = null;
		Connection conn = null;
		int event_id = 0;
		try {
			conn = getDbConnection();
			preparedStatement = conn.prepareStatement(selectSQL);
			preparedStatement.setInt(1, optionid);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				event_id = rs.getInt("event_id");
				option = new Option();
				option.id = optionid;
				option.name = name;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}
		option.event = getEvent(event_id);
		return option;
	}

	@Override
	public boolean eventTypesEstablished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void insertEventType(EventType eventType) {
		String insertTableSQL = "INSERT INTO event_type"
				+ "(event_type_id,name) VALUES" + "(?,?)";
		Connection conn = getDbConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(insertTableSQL,
					Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, eventType.getEventType());
			preparedStatement.setString(2, eventType.name());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			closeGracefully(conn, preparedStatement);
		}		
	}

}
