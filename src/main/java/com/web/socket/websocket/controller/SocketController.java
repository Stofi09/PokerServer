package com.web.socket.websocket.controller;



import com.web.socket.websocket.bean.MessageBean;
import com.web.socket.websocket.game.Card;
import com.web.socket.websocket.game.Game;
import com.web.socket.websocket.game.Player;
import com.web.socket.websocket.result.Result;

import java.util.ArrayList;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {
	
	
	private static int turn = 0;
	
	private static boolean hasDisconnected = false;
	
	@Autowired
	private Result res;
	@Autowired
	private static Game game;
	@Autowired
	private Player player;
	@Autowired
	private Player player2;


    @MessageMapping("/Join")
    @SendTo("/topic/user")
    public MessageBean sendToAll(@Payload MessageBean message , @Header("simpSessionId") String sessionId) {
    	
    	setHasDisconnectedtoFalse();
    	System.out.println(sessionId);
    	if(game == null ) {
    		game = new Game();
    	//	player = new Player(message.getName(),1000,sessionId);
    		game.setPlayer(message.getName(),1000,sessionId);
    		System.out.println(message.getName() + " " + sessionId);
    		message.setCredit(1000);
    	}else {
    	//	player2 = new Player(message.getName(),1000,sessionId);
    		game.setPlayer(message.getName(),1000,sessionId);
    		message.setOppName(game.getOpponentName(message.getName()));
    		System.out.println(message.getName() + " " + sessionId);
    		message.setCredit(1000);
    		message.setOppCredit(1000);
    	}
    	
    	
    	
    	message.setType("Join");
        return message;
    }
    
  
    
    @MessageMapping("/sendback")
    @SendTo("/topic/user")
    public MessageBean sendthat(@Payload MessageBean message) {
    	message.setType("sikerult");
    	return message;
    }
   
    
    @MessageMapping("/start")
    @SendTo("/topic/user")
    public MessageBean sendStart(@Payload MessageBean message) {
    	if (hasDisconnected) {
    		message.setType("otherLeft");
        	return message;
    	}else {
    		
    	game.nullTurn();
    	turn = game.turnCounter();
    	System.out.println(player.getName() + " and " + player2.getName());
    	ArrayList<Card> deck = new ArrayList<>();
		deck = Card.makeDeck();
		Collections.shuffle(deck);
		player = new Player(game.getPlayer1().getName());
		this.res.setResult(deck.get(0),deck.get(1),deck.get(4),deck.get(5), deck.get(6), deck.get(7), deck.get(8));
		this.res.checkBooleans();
		this.res.setPlayerResult(player);
		System.out.println(player.toString2() );
		System.out.println("-------------------");
		player2 = new Player(game.getPlayer2().getName());
		this.res.setResult(deck.get(2),deck.get(3),deck.get(4),deck.get(5), deck.get(6), deck.get(7), deck.get(8));		
		this.res.checkBooleans();
		this.res.setPlayerResult(player2);
		System.out.println(player2.toString2() );
		System.out.println(player.toString2() + " and " + player2.toString2());
		
		message.setResult(Result.checkWinner(player, player2));
		
		System.out.println(message.getResult());
		int[] cards = {deck.get(0).getId(),deck.get(1).getId(),deck.get(2).getId(),deck.get(3).getId(),deck.get(4).getId(),deck.get(5).getId(),deck.get(6).getId(),deck.get(7).getId(),deck.get(8).getId()};
    	message.setCards(cards);
		message.setType("Start");
    	return message;
    	}
    }
    
    @MessageMapping("/check")
    @SendTo("/topic/user")
    public MessageBean sendCheck(@Payload MessageBean message) {
    	if (hasDisconnected) {
    		message.setType("otherLeft");
        	return message;
    	}else {
    	game.setCheckTurned(message.getName());
    	turn  = game.turnCounter();
    	message.setType("Check");
    	message.setTurn(turn);
    	return message;
    	}
    }
    
    @MessageMapping("/firstRaise")
    @SendTo("/topic/user")
    public MessageBean sendRaise(@Payload MessageBean message) {
    	if (hasDisconnected) {
    		message.setType("otherLeft");
        	return message;
    	}else {
    	game.setRaiseTurned(message.getName());
    	turn  = game.turnCounter();
    	message.setTurn(turn);
    	//turn = game.setRaiseTurn(message.isRaise(), bol2, bol3)
    	System.out.println("Raise lefutott" + message.getCredit());
    	return message;
    	}
    }
    
    @MessageMapping("/equalCall")
    @SendTo("/topic/user")
    public MessageBean sendEqualCall(@Payload MessageBean message) {
    	if (hasDisconnected) {
    		message.setType("otherLeft");
        	return message;
    	}else {
    	game.setRaiseTurned(message.getName());
    	//turn = game.setRaiseTurn(message.isRaise(), bol2, bol3)
    	System.out.println("equalCall lefutott" + message.getCredit());
    	return message;
    	}
    }
    
    @MessageMapping("/overCall")
    @SendTo("/topic/user")
    public MessageBean sendOverCall(@Payload MessageBean message) {
    	if (hasDisconnected) {
    		message.setType("otherLeft");
        	return message;
    	}else {
    	game.setRaiseTurned(message.getName());
    	//turn = game.setRaiseTurn(message.isRaise(), bol2, bol3)
    	System.out.println("overCall lefutott" + message.getCredit());
    	return message;}
    }
    
    @MessageMapping("/calledOverRaise")
    @SendTo("/topic/user")
    public MessageBean sendCalledOverRaise(@Payload MessageBean message) {
    	if (hasDisconnected) {
    		message.setType("otherLeft");
        	return message;
    	}else {
    	game.setRaiseTurned(message.getName());
    	//turn = game.setRaiseTurn(message.isRaise(), bol2, bol3)
    	System.out.println("calledOverRaise lefutott" + message.getCredit());
    	return message;}
    }
    
    @MessageMapping("/fold")
    @SendTo("/topic/user")
    public MessageBean sendFold(@Payload MessageBean message) {
    	if (hasDisconnected) {
    		message.setType("otherLeft");
        	return message;
    	}else {
    	game.nullTurn();
    	turn = game.turnCounter();
    	
    	message.setType("Fold");
    	message.setTurn(0);
    	return message;}
    }
    
    public void removePlayer(String id) {
    	game.nullPlayer(id);
    	
    }
    public static void setHasDisconnectedToTrue() {
    	hasDisconnected = true;
    }

    private void setHasDisconnectedtoFalse() {
    	hasDisconnected = false;
    }
}
