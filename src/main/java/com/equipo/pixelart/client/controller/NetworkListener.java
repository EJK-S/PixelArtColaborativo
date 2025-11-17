package com.equipo.pixelart.client.controller;

import java.awt.Color;

public interface NetworkListener {
    default void onConnectionStatusChanged(boolean connected, String message) {}
    default void onAuthResult(boolean success, String message) {}
    default void onChatMessage(String user, String message) {}
    default void onUserJoined(String user) {}
    default void onUserLeft(String user) {}
    default void onUserListReceived(String[] users) {}
    default void onError(String message) {}
    default void onPixelUpdateReceived(int x, int y, Color color) {}
    default void onCanvasStateReceived(Color[][] canvas) {}
    default void onCanvasCleared() {}
}