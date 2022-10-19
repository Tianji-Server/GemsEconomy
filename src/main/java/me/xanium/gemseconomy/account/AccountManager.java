/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.account;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.utils.SchedulerUtils;
import me.xanium.gemseconomy.utils.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class AccountManager {

    private GemsEconomy plugin;
    private CopyOnWriteArrayList<Account> accounts;

    public AccountManager(GemsEconomy plugin) {
        this.plugin = plugin;
        this.accounts = new CopyOnWriteArrayList<>();
    }

    public void createAccount(String nickname) {
        createAccount(Bukkit.getOfflinePlayer(nickname).getUniqueId(), nickname);
    }

    public synchronized void createAccount(UUID uuid, String name) {
        Account account = getAccount(uuid);
        String playerName = name;

        if (playerName == null || playerName.isEmpty())
            playerName = Bukkit.getOfflinePlayer(uuid).getName();
        if (playerName == null || playerName.isEmpty())
            playerName = "Unknown";
        if (account == null) {
            account = new Account(uuid, playerName);
            add(account);
            account.setCanReceiveCurrency(true);
            account.setNickname(playerName);

            if (!plugin.getDataStore().getName().equalsIgnoreCase("yaml")) {
                // MYSQL
                plugin.getDataStore().createAccount(account);
            } else {
                // YAML
                plugin.getDataStore().saveAccount(account);
            }
            UtilServer.consoleLog("New Account created for: " + account.getDisplayName());
        }
    }

    public Account getAccount(Player player) {
        return getAccount(player.getUniqueId());
    }

    public Account getAccount(String name) {
        for (Account account : this.accounts) {
            if (account.getNickname() == null || !account.getNickname().equalsIgnoreCase(name)) continue;
            return account;
        }
        return plugin.getDataStore().loadAccount(name);
    }

    public Account getAccount(UUID uuid) {
        for (Account account : this.accounts) { // This throws CME randomly
            if (!account.getUuid().equals(uuid)) continue;
            return account;
        }
        return plugin.getDataStore().loadAccount(uuid);
    }

    public void removeAccount(UUID uuid) {
        for (int i = 0; i < this.accounts.size(); i++) {
            Account a = getAccounts().get(i);
            if (a.getUuid().equals(uuid)) {
                accounts.remove(i);
                break;
            }
        }
    }

    public void add(Account account) {
        if (this.accounts.contains(account)) return;

        this.accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Account> getAllAccounts() {
        return plugin.getDataStore().getOfflineAccounts();
    }
}

