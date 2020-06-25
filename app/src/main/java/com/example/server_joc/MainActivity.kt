package com.example.server_joc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.*
import java.lang.Exception
import java.net.InetAddress
import java.net.ServerSocket
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val server = ServerSocket(1234,10, InetAddress.getByName("0.0.0.0"))
        val database = FirebaseDatabase.getInstance();
        val databaseReference = database.getReference();

        thread {
            run {
                try {

                    thread {
                        run {try {
                            while(true){
                                val client = server.accept();
                                System.out.println("LINE 1")
                                println("Client connected: ${client.inetAddress.hostAddress}");
                                val reader = BufferedReader(InputStreamReader(client.getInputStream()));
                                System.out.println("LINE 2")
                                while (true) {
                                    val read = reader.readLine();
                                    val line = read.toString();
                                    System.out.println(line);
                                    val endindexCommand = line.indexOf('*');
                                    val command = line.substring(0, endindexCommand);
                                    System.out.println("LINE 3")
                                    if (command.compareTo("Save Game") == 0) {
                                        val endindexName = line.indexOf(',');
                                        val username = line.substring(endindexCommand + 1, endindexName);
                                        val score = line.substring(endindexName, line.length);
                                        databaseReference.child(username).child("")
                                                .setValue(username + score);

                                    }
                                    System.out.println("LINE 4")
                                    if (command.compareTo("Get Game") == 0) {
                                        System.out.println("StilL LISTEN")
                                        var data: Array<String?> = arrayOfNulls(55)
                                        var cont = 0
                                        var wait = false
                                        System.out.println("LINE 5")
                                        databaseReference.addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                                for (user in dataSnapshot.children) {
                                                    System.out.println(client.toString());
                                                    cont++;
                                                    System.out.println(cont)
                                                    data.set(cont, user.value as String)
                                                    System.out.println(data.get(cont));
                                                    wait = false
                                                }
                                                wait = true
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {}

                                        })
                                        while (wait == false) {
                                            System.out.println("ASTEPTAM")
                                        }
                                        System.out.println("LINE 6")
                                        for (i in 1..cont) {
                                            System.out.println("--------------- : " + data.get(i))
                                            val writer = PrintWriter(
                                                    BufferedWriter(OutputStreamWriter(client.getOutputStream())),
                                                    true
                                            )
                                            writer.println(data.get(i))
                                            System.out.println(data.get(i))
                                            writer.flush()
                                        }


                                    }

                                }
                                System.out.println("LINE 7")
                            }

                        }catch (e : Exception){
                            System.out.println("Exception");
                            server.close();
                        }



                        }
                    }
                }catch (e : Exception){
                    System.out.println("Exception");
                }
            }}}}

