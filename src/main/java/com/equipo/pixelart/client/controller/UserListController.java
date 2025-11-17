package com.equipo.pixelart.client.controller;

import com.equipo.pixelart.client.view.UserListPanel;

public class UserListController implements NetworkListener {

    private final UserListPanel userListPanel;
    private final NetworkController networkController;

    public UserListController(UserListPanel userListPanel, NetworkController networkController) {
        this.userListPanel = userListPanel;
        this.networkController = networkController;
    }

    public void init() {
       
        networkController.addNetworkListener(this);
    }

    @Override
    public void onUserJoined(String u) {
        userListPanel.addUser(u);
    }

    @Override
    public void onUserLeft(String u) {
        userListPanel.removeUser(u);
    }

    @Override
    public void onUserListReceived(String[] u) {
        userListPanel.setUsers(u);
    }

    @Override
    public void onConnectionStatusChanged(boolean connected, String msg) {
        
        if (!connected) {
            userListPanel.clearUsers();
        }
    }
}