/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tes1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author Acer
 */
public class simpleBot extends TelegramLongPollingBot {

    Connection Con;
    ResultSet RsMember;
    ResultSet RsKeyword;
    ResultSet RsUser;
    Statement stm;
    Long idUser1;
    String namaUser1;
    String logPesan;
    String message = "command Tidak Tersedia";
    String command;
    private frmDataMember frmDM;
    private frmLogPesan frmLP;
    private frmKeyword frmKW;
    private frmDataUser frmDU;
    private frmMenu frmM;
    private LoginAdmin frmLA;
    private frmRegisterAdmin frmRA;

    public simpleBot(frmDataMember frmDM) {
        this.frmDM = frmDM;
    }

    public simpleBot(frmLogPesan frmLP) {
        this.frmLP = frmLP;
    }

    public simpleBot(frmKeyword frmKW) {
        this.frmKW = frmKW;
    }

    public simpleBot(frmDataUser frmDU) {
        this.frmDU = frmDU;
    }

    public simpleBot(frmMenu frmM) {
        this.frmM = frmM;
    }

    public simpleBot(LoginAdmin frmLA) {
        this.frmLA = frmLA;
    }

    public simpleBot(frmRegisterAdmin frmRA) {
        this.frmRA = frmRA;
    }

    @Override
    public void onUpdateReceived(Update update) {

        open_db();
        logPesan = update.getMessage().getText();
        idUser1 = update.getMessage().getFrom().getId();
        namaUser1 = update.getMessage().getFrom().getFirstName();
        command = update.getMessage().getText();
        message = "command Tidak Tersedia";

//        frmLP.setLogPesanAll(namaUser1,message);
        addDataUser(idUser1, namaUser1);

        if (command.equals("/start")) {
            message = "/register - mendaftar member\n" + "/help - daftar perintah";
            simpanLogPesanDB(namaUser1, message, command, idUser1);
            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);

            try {
                execute(response);
                frmLP();
//                message = "command Tidak Tersedia";
//                return;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
        if (command.equals("/register")) {
            Long idUser = update.getMessage().getFrom().getId();
            String namaUser = update.getMessage().getFrom().getFirstName();

            try {
                stm = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                String sql = "SELECT * FROM member";
                RsMember = stm.executeQuery(sql);
                boolean reg = true;
                while (RsMember.next() && reg == true) {
                    Long column1Value = RsMember.getLong("id_member");
                    if (idUser1.equals(column1Value)) {
                        message = "Anda Sudah Menjadi Member";
                        simpanLogPesanDB(namaUser1, message, command, idUser1);
                        SendMessage response = new SendMessage();
                        response.setChatId(update.getMessage().getChatId().toString());
                        response.setText(message);
                        try {
                            execute(response);
                            reg = false;
                            frmLP();
//                            message = "command Tidak Tersedia";
                            break;
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (reg == true) {
                    try {
                        stm.executeUpdate("INSERT into member VALUES (" + idUser + ",'" + namaUser + "')");
                        message = "Berhasil mendaftar Member";
                        simpanLogPesanDB(namaUser1, message, command, idUser1);
                        SendMessage response = new SendMessage();
                        response.setChatId(update.getMessage().getChatId().toString());
                        response.setText(message);
                        try {
                            execute(response);
                            frmLP();
//                            message = "command Tidak Tersedia";
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(simpleBot.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(simpleBot.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        if (!command.equals("/start") && !command.equals("/register")) {
            try {
                stm = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                String sql1 = "SELECT * FROM keyword";
                RsKeyword = stm.executeQuery(sql1);

                stm = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                String sql = "SELECT * FROM member";
                RsMember = stm.executeQuery(sql);
                boolean val = true;
                while (RsMember.next() && val == true) {
                    Long column1Value = RsMember.getLong("id_member");

                    if (idUser1.equals(column1Value)) {
                        keywordDb();
                        
//                        LocalDate currentDate = LocalDate.now();
//                        stm = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//                        stm.executeUpdate("INSERT into logpesan VALUES (" + null + ",'" + namaUser1 + "','" + "PK" + "','" + command + "'," + idUser1 + ",'" + currentDate + "')");

                        simpanLogPesanDB(namaUser1, message, command, idUser1);
                        val = false;
                        frmLP();
//                        message = "command Tidak Tersedia";
                        break;
                    }
                }
                if (val == true) {
                    message = "mendaftar Member Dulu !";
                    simpanLogPesanDB(namaUser1, message, command, idUser1);
//                    frmLP.setLogPesan("Server : " + message);
                    SendMessage response = new SendMessage();
                    response.setChatId(update.getMessage().getChatId().toString());
                    response.setText(message);
                    try {
                        execute(response);
                        frmLP();
//                        message = "command Tidak Tersedia";
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

            } catch (SQLException e) {
                System.out.println(e);

            }
        }

        if (command.equals("/keluar_member")) {
            try {
                stm = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                String sql = "SELECT * FROM member";
                RsMember = stm.executeQuery(sql);
                while (RsMember.next()) {
                    Long tempID = RsMember.getLong("id_member");
                    if (tempID.equals(idUser1)) {
                        message = "Anda sudah keluar member";
                        String sql1 = "delete from member where id_member=" + idUser1 + "";
                        String sql2 = "delete from logpesan where id_member=" + idUser1 + "";
                        stm.executeUpdate(sql2);
                        stm.executeUpdate(sql1);
                        SendMessage response = new SendMessage();
                        response.setChatId(update.getMessage().getChatId().toString());
                        response.setText(message);
                        try {
                            execute(response);
                            frmLP();
//                            message = "command Tidak Tersedia";
                            break;
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (SQLException e) {
            }
        }
        if (message.equals("command Tidak Tersedia")) {
            SendMessage response = new SendMessage();
            simpanLogPesanDB(namaUser1, message, command, idUser1);

            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);
            try {
                execute(response);
                frmLP();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

//        frmLP();
    }

    @Override
    public String getBotUsername() {
        // TODO
        return "azizu9bot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "6396163593:AAGIS0t9u9YLVY6acQoPUuQBQjKlnfG2iVY";
    }

    private void open_db() {

        try {
            Koneksi kon = new Koneksi("localhost", "root", "", "bot");
            Con = kon.getConnection();
//            System.err.println("berhasil");
        } catch (Exception e) {
            System.err.println("Eror : " + e);
        }

    }

    public void simpanLogPesanDB(String Username, String PesanKeluar, String PesanMasuk, Long IdMember) {
        open_db();
        try {
            LocalDate currentDate = LocalDate.now();
            stm = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stm.executeUpdate("INSERT into logpesan VALUES (" + null + ",'" + Username + "','" + PesanKeluar + "','" + PesanMasuk + "'," + IdMember + ",'" + currentDate + "')");
        } catch (Exception e) {
        }
//        try {
//            String sql1 = "INSERT INTO logpesan (id, username, pesan_keluar, pesan_masuk, id_member, date) VALUES (?, ?, ?, ?, ?, ?)";
//            PreparedStatement preparedStatement = Con.prepareStatement(sql1);
//            preparedStatement.setNull(1, java.sql.Types.NULL);
//            preparedStatement.setString(2, Username);
//            preparedStatement.setString(3, PesanKeluar);
//            preparedStatement.setString(4, PesanMasuk);
//            preparedStatement.setLong(5, IdMember);
//            Timestamp timestampValue = new Timestamp(System.currentTimeMillis()); // Replace with your desired timestamp value
//            preparedStatement.setTimestamp(6, timestampValue);
//            preparedStatement.executeUpdate();
//        } catch (Exception e) {
//        }

    }

    public void frmLP() {
        String name = "Server";
        frmLP.setLogPesanAll(namaUser1, logPesan);
        frmLP.setLogPesanAll(name, message);
    }

    public void broadcast(String message) {
        open_db();
        ArrayList<Long> list = new ArrayList<>();
        try {
            stm = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM member";
            RsMember = stm.executeQuery(sql);
            while (RsMember.next()) {
                list.add(RsMember.getLong("id_member"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        for (Long IdTele : list) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(IdTele);
            sendMessage.setText(message);
            try {
                execute(sendMessage);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

    }

    public void keywordDb() {
        open_db();
        try {
            LocalDate currentDate = LocalDate.now();
            stm = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM keyword";
            RsKeyword = stm.executeQuery(sql);
            while (RsKeyword.next()) {
                String tempKey = RsKeyword.getString("keyword");
                String tempDes = RsKeyword.getString("deskripsi");

                if (command.equals(tempKey)) {
                    message = tempDes;
//                    simpanLogPesanDB(namaUser1, message, command, idUser1);
                    SendMessage response = new SendMessage();
                    response.setChatId(idUser1);
                    response.setText(message);
                    try {
                        execute(response);

                        break;
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    public void addDataUser(Long id, String Username) {
        open_db();
        boolean user = false;
        try {
            stm = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM user where id_user = " + String.valueOf(id) + "";
            RsUser = stm.executeQuery(sql);
            int baris = 0;
            while (RsUser.next()) {
                baris = RsUser.getRow();
            }
            if (baris == 0) {
                stm.executeUpdate("INSERT into user VALUES (" + id + ",'" + Username + "')");
            }
//            while (RsUser.next()) {
//                Long tempId = RsUser.getLong("id_user");
//                String tempid2 = String.valueOf(tempId);
//
//                if (id == tempId) {
//                    user = true;
//
//                    return;
//                }
//
//            }
//            if (user == false) {
//                stm.executeUpdate("INSERT into user VALUES (" + id + ",'" + Username + "')");
//            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

}
