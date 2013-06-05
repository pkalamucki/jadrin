package main.jadrin.waiter;

import java.nio.charset.Charset;

import main.jadrin.ontology.QueryOntology;
import main.jadrin.tools.Tokenizer;
import morfologik.stemming.IStemmer;
import morfologik.stemming.PolishStemmer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BuildQueryBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7602120656772135185L;
	private String query;
	private AID[] bartenders;
	private WaiterAgentGui gui;
	BuildQueryBehaviour(Agent agent, String query, AID[] bartenders, WaiterAgentGui gui)
	{
		super(agent);
		this.query = query;
		this.bartenders = bartenders;		
		this.gui = gui;
	}
	
	@Override
	public void action() {
		boolean drinkQuery = false;
		
		IStemmer stemmer = new PolishStemmer();
		String[] notPermitted = {"prep"};
		String[] tokens = Tokenizer.getTokens(stemmer, query, notPermitted);
		
		String drink = "";
		for (String str: tokens)
		{
			ACLMessage reply = new ACLMessage(ACLMessage.REQUEST);
			reply.addReceiver(bartenders[0]);
			reply.setOntology(QueryOntology.NAME);
			reply.setContent(str);
			myAgent.send(reply);
			ACLMessage msg = myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			System.out.println("Checked: " + str + " Result: " + msg.getContent());
			if (msg.getContent().equals("drink"))
			{
				drinkQuery = true;
				drink = str;
			}
		}
		
		String result;
		if (drinkQuery)
		{
			result = "Spytałeś o skład " + drink;
		}
		else
		{
			result =" Nie znam odpowiedzi na to pytanie";
		}
		
		gui.setResponse(result);
		gui.setEditable(true);
		

	}

}
