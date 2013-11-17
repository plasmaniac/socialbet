package no.gnet.edvd;

import javax.sql.DataSource;

import junit.framework.Assert;
import no.gnet.edvd.database.inmemory.InMemoryDatastore;
import no.gnet.edvd.database.mysql.MySQLDatastore;
import no.gnet.edvd.engines.DefaultEngine;
import no.gnet.edvd.engines.EventOdds;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class MysqlTest {

	@Autowired
	protected DataSource datasource = null;

	Player jimmy = null;
	DataStore mysqlds;
	
	@Before
	public void setup() {
		jimmy = new Player("jimmy", "jimmyson", "jimmy@jimson.com");

		mysqlds = new MySQLDatastore(datasource);
		_setupCurrency();
		_setupEventTypes();
		_setupPlayers();
		
	}


	private void _setupEventTypes() {
		if(!mysqlds.eventTypesEstablished()){
			
			try {
				mysqlds.insertEventType(EventType.CENTRALLY_MANAGED);
				mysqlds.insertEventType(EventType.PLAYER_MANAGED);
			} catch (Exception e) {
				// IGNORE
			}
			}		
	}


	private void _setupCurrency() {
		if(!mysqlds.currenciesEstablished()){
			
		try {
			mysqlds.insertCurrency(Currency.NOK);
			mysqlds.insertCurrency(Currency.EURO);
			mysqlds.insertCurrency(Currency.VIRTUALCOIN);
			mysqlds.insertCurrency(Currency.BITCOIN);
		} catch (Exception e) {
			// IGNORE
		}
		}
	}

	private void _setupPlayers() {
		if(!mysqlds.playerExists(jimmy)){
			jimmy=mysqlds.storePlayer(jimmy);
		} else {
			jimmy=mysqlds.getPlayer(jimmy);
		}
	}

	@Test
	public void testPlaceBetYesNoEvent() {
		DefaultEngine engine = new DefaultEngine();
		Event sitterRegjeringen = getYesNoEvent("Sitter den nye regjeringen hele perioden?");		
		Event storedEvent = mysqlds.storeEvent(sitterRegjeringen);
		StringOption yesoption = (StringOption) storedEvent
				.getOptionByName("YES");
		Bet yesbet = new Bet(storedEvent, yesoption, 100.0, Currency.NOK, jimmy);
		int betid = mysqlds.placeBet(yesbet);
		yesbet.id=betid;
		Assert.assertNotNull(mysqlds.getBet(yesbet));
	}
	
	@Test
	public void testMatchBet(){
		DefaultEngine engine = new DefaultEngine();

		Event evliv = new MatchEvent("Everton", "Liverpool");
		DateTime inAMonth= new DateTime();
		inAMonth=inAMonth.plusMonths(1);
		evliv.betDeadline=inAMonth;
		evliv.eventStartTime=inAMonth;
		Event storedEvent = mysqlds.storeEvent(evliv);
		Option livOption = storedEvent.getOptionByName("Liverpool");
		Option everton= storedEvent.getOptionByName("Everton");
		Option draw = storedEvent.getOptionByName("draw");
		Bet liverbet = createBet(storedEvent, livOption, 5000.0);
		Bet drawbet = createBet(storedEvent, draw, 100.0);
		Bet everbet = createBet(storedEvent, everton, 400.0);
		mysqlds.placeBets(liverbet, drawbet, everbet);
		EventOdds oddsle = engine.getOddsForEvent(evliv, mysqlds);

		Assert.assertTrue(oddsle.get(new StringOption("Liverpool")) == 1.1);
		Assert.assertTrue(oddsle.get(new StringOption("Everton")) == 13.75D);		
	}


	private Bet createBet(Event storedEvent, Option everton, double amount) {
		Bet everbet = new Bet(storedEvent, everton, amount,
				Currency.NOK,jimmy, InMemoryDatastore.inVMautoInc());
		return everbet;
	}
	
	
	@Test
	public void testStoreEvents() {
		String q  = "Vil Listhaug sitte til jul?";
		Event anEvent = getYesNoEvent(q);
		Event storedEvent = mysqlds.storeEvent(anEvent);
		Assert.assertTrue(storedEvent.name.equals(q));
	}
		
	
	

	private Event getYesNoEvent(String q) {
		Event sitterRegjeringen = new YesNoEvent(q);
		sitterRegjeringen.description = q;
		sitterRegjeringen.id = InMemoryDatastore.inVMautoInc();
		DateTime inAMonth= new DateTime();
		inAMonth=inAMonth.plusMonths(1);
		sitterRegjeringen.betDeadline=inAMonth;
		sitterRegjeringen.eventStartTime=inAMonth;		
		return sitterRegjeringen;
	}
}
