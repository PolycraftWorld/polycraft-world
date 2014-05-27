package edu.utd.minecraft.mod.polycraft.util;

import java.io.IOException;

import javax.security.auth.login.FailedLoginException;

import org.wikipedia.Wiki;


public class WikiMaker {

	public static void main(String[] args) {
		try {
			WikiMaker wikiMaker = new WikiMaker();
			wikiMaker.createWiki();
		} catch(Exception ex) {
			System.out.println("Failed: " + ex.getMessage());
		}
	}

	public void createWiki() throws FailedLoginException, IOException {
		Wiki wiki = new Wiki("www.polycraftworld.com", "/wiki");
		wiki.login("Polycraftbot", "gmratst6zf");		
		wiki.logout();
	}
}
