package com.wordpress.musicamovelbahia.multigranular.pdstuff;

public interface PdListenerCallBack {
	void callWhenReceiveFloat(String key, float val);
	void callWhenReceiveBang(String key);
	void callWhenReceiveSymbol(String key, String symbol);
	void callWhenReceiveList(String key, Object... args);
	void callWhenReceiveMessage(String key, String symbol, Object... args);
}
