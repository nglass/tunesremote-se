package net.firefly.client.controller.request;

import java.net.URL;

import net.firefly.client.controller.listeners.SongListLoadProgressListener;
import net.firefly.client.model.data.Database;
import net.firefly.client.model.data.list.SongList;
import net.firefly.client.tools.FireflyClientException;

public interface IRequestManager {
	
	public boolean isAuthenticationRequired(String host, int port, String proxyHost, String proxyPort)
			throws FireflyClientException;

	public boolean checkAuthentication(String host, int port, String proxyHost, String proxyPort, String password)
			throws FireflyClientException;

	public Database getDatabase(String host, int port, String proxyHost, String proxyPort, String password)
			throws FireflyClientException;

	public SongList getSongList(String host, int port, String proxyHost, String proxyPort, String password)
			throws FireflyClientException;

	public SongList getSongList(String host, int port, String proxyHost, String proxyPort, String password,
			SongListLoadProgressListener listener) throws FireflyClientException;

	public void updateSongList(SongList songList, String host, int port, String proxyHost, String proxyPort,
			String password, long overDatabaseId) throws FireflyClientException;

	public void updateSongList(SongList songList, String host, int port, String proxyHost, String proxyPort,
			String password, long overDatabaseId, SongListLoadProgressListener listener) throws FireflyClientException;

	public URL getSongStreamURL(String host, int port, int itemId) throws FireflyClientException;

	public void setUseHttpCompressionWhenPossible(boolean use);

	public boolean getUseHttpCompressionWhenPossible();

}