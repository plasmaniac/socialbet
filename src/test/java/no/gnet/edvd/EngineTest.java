package no.gnet.edvd;

import junit.framework.Assert;
import no.gnet.edvd.database.inmemory.InMemoryDatastore;
import no.gnet.edvd.engines.DefaultEngine;
import no.gnet.edvd.engines.EventOdds;

import org.junit.Test;

public class EngineTest {

	@Test
	public void testBasicAlgorithm() {
		DataStore imds = new InMemoryDatastore();
		Player jimmy = new Player("jimmy", "jimmyson","jimmy@jimson.com");
		jimmy.id=InMemoryDatastore.inVMautoInc();
		DefaultEngine engine = new DefaultEngine();
		Event sitterRegjeringen = getYesNoEvent("Sitter den nye regjeringen hele perioden?");
		imds.storeEvent(sitterRegjeringen);
		StringOption yesoption = StringOption.YES(sitterRegjeringen);
		Bet yesbet = new Bet(sitterRegjeringen, yesoption, 100.0, Currency.NOK, jimmy);
		yesbet.id = InMemoryDatastore.inVMautoInc();
		StringOption nooption = StringOption.NO(sitterRegjeringen);
		Bet nobet = new Bet(sitterRegjeringen, nooption, 200.0, Currency.NOK,jimmy);
		nobet.id = InMemoryDatastore.inVMautoInc();
		imds.placeBet(yesbet);
		imds.placeBet(nobet);
		EventOdds odds = engine.getOddsForEvent(sitterRegjeringen,
				imds);
		Assert.assertTrue(odds.get(yesoption) == 3.0);
		Assert.assertTrue(odds.get(nooption) == 1.5);

		Event evliv = new MatchEvent("Everton", "Liverpool");
		evliv.id = InMemoryDatastore.inVMautoInc();
		imds.storeEvent(evliv);
		Bet liverbet = new Bet(evliv, new StringOption("Liverpool"), 5000.0,
				Currency.NOK,jimmy, InMemoryDatastore.inVMautoInc());
		Bet drawbet = new Bet(evliv, new StringOption("draw"), 100.0,
				Currency.NOK,jimmy, InMemoryDatastore.inVMautoInc());
		Bet everbet = new Bet(evliv, new StringOption("Everton"), 400.0,
				Currency.NOK,jimmy, InMemoryDatastore.inVMautoInc());
		imds.placeBets(liverbet, drawbet, everbet);
		EventOdds oddsle = engine.getOddsForEvent(evliv, imds);

		Assert.assertTrue(oddsle.get(new StringOption("Liverpool")) == 1.1);
		Assert.assertTrue(oddsle.get(new StringOption("Everton")) == 13.75D);

	}

	private Event getYesNoEvent(String q) {
		Event sitterRegjeringen = new YesNoEvent(q);
		sitterRegjeringen.id = InMemoryDatastore.inVMautoInc();
		return sitterRegjeringen;
	}

	public void testPlaceBet() {

	}

	@Test
	public void testListEvents() {
		DataStore imds = new InMemoryDatastore();
		Event e = getYesNoEvent("Sitter Listhaug til 2014?");
		imds.storeEvent(e);
		Assert.assertTrue(imds.listEvents()!=null);
		Assert.assertTrue(imds.listEvents().size()>0);
		
		

	}

}
