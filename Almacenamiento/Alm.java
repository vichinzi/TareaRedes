import java.util.Scanner;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;

import java.sql.Timestamp;

public class Alm {
    public static void main(String[] args) {
        try {
            final int PUERTO = 11111;

            ServerSocket server = new ServerSocket(PUERTO);

            System.out.println("Almacenamiento iniciado");
            while (true) {                                              
                Socket socketpropio = server.accept();                                         
                new ThreadSocket(socketpropio);
                System.out.println("Servidor conectado");
            }
        } catch (Exception e) {
        }
    }


    public void lss(DataOutputStream oe,String socketIP) throws Exception{
        
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String loguear = "\n"+time+"\tcommand\t\t"+socketIP+" ls\n";
        File log = new File("log.txt");
        BufferedWriter w;
        w = new BufferedWriter(new FileWriter(log,true));
        w.newLine();
        w.write(loguear);
        w.close();

        File dir = new File(System.getProperty("user.dir"));
        File[] uwu = dir.listFiles();
        for (File file : uwu){
            oe.writeUTF(file.getName()); //(-><-)
        }
        oe.writeUTF("Finisher"); //(2) segunda ida al servidor
        oe.writeUTF("ls oe");

        time = new Timestamp(System.currentTimeMillis());
        loguear = "\n"+time+"\tresponse\t"+"servidor envia respuesta a"+ socketIP+"\n";
        w = new BufferedWriter(new FileWriter(log,true));
        w.newLine();
        w.write(loguear);
        w.close();

    }

    public void enviar(String palabra, Socket socket) throws Exception {

        String concatenacion = System.getProperty("user.dir") + "/" + palabra;
        System.out.println(concatenacion);
        File myFile = new File(concatenacion);
        int largo = (int) myFile.length(); //funciona
        String largito = Integer.toString(largo);


        DataOutputStream osss = new DataOutputStream(socket.getOutputStream());
        osss.writeUTF(largito);
        DataInputStream issss = new DataInputStream(socket.getInputStream());
        issss.readUTF();
        if(issss.equals("gracias")){
            System.out.println("llego el gracias");
        }

        byte[] mybytearray = new byte[largo];
        FileInputStream fis = new FileInputStream(concatenacion);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedOutputStream outt = new BufferedOutputStream(socket.getOutputStream());

        int current;
        current = bis.read(mybytearray);
        outt.write(mybytearray,0, current);


        
        //bis.read(mybytearray, 0, mybytearray.length);
        System.out.println("Archivo enviado");
        //os.write(mybytearray, 0, mybytearray.length);
        outt.flush();
        fis.close();
        bis.close();

    }

    public void recivir(Socket socket, String name) throws Exception {

        String largo;
        int largito;
        int bytesRead;
        int current = 0;
        String nombre = name;

        DataInputStream isss = new DataInputStream(socket.getInputStream());
        largo = isss.readUTF();
        largito = Integer.parseInt(largo);
        System.out.println(largito);

        DataOutputStream osss = new DataOutputStream(socket.getOutputStream());
        osss.writeUTF("gracias");

        DataInputStream iss = new DataInputStream(socket.getInputStream());
        byte[] mybytearray = new byte[largito];
        FileOutputStream fos = new FileOutputStream(nombre);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        iss.readFully(mybytearray); //nunca esta leyendo el -1 porqueeee!!!

        bos.write(mybytearray, 0, largito);

        //bos.write(mybytearray, 0, bytesRead);
        //bos.flush();
        System.out.println("Archivo creado");
        bos.close();
        fos.close();
    }
}

class ThreadSocket extends Thread{
    private Socket sc;    
    ThreadSocket(Socket insocket){
        this.sc = insocket;
        this.start();
    }       
    @Override
    public void run() {     
        try {
            String socketIP = sc.getRemoteSocketAddress().toString();

            DataInputStream in;
            File filemanager;
            
            String mensaje;

             in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            //Leo el mensaje que me envia el server
            mensaje = in.readUTF();


            if(mensaje.equals("ls")){
                System.out.println("uwu");
            }

            
                

                /*
                else if(mensaje.split(" ")[0].equals("get")) {
                    //OutputStream out = sc.getOutputStream();
                    String[] palabras = mensaje.split(" ");
                    String palabra = palabras[1].replaceAll("\\s","");
                    System.out.println(palabra);
                    try {
                        new Server().enviar(palabra,sc);
                    } catch (Exception ex) {
                        Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                else if(mensaje.split(" ")[0].equals("put")) {
                    String[] palabras = mensaje.split(" ");
                    String palabra = palabras[1].replaceAll("\\s","");
                    DataOutputStream out = new DataOutputStream(sc.getOutputStream());
                    out.writeUTF(palabra);
                    //InputStream ins = sc.getInputStream();
                    try {
                        new Server().recivir(sc,palabra);
                    } catch (Exception ex) {
                        Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                else if(mensaje.split(" ")[0].equals("delete")){
                    DataOutputStream out = new DataOutputStream(sc.getOutputStream());
                    String cwd = System.getProperty("user.dir") + "/" + mensaje.split(" ")[1];
                    System.out.println(cwd);
                    filemanager = new File(cwd);
                    if(filemanager.delete()){
                        out.writeUTF("Archivo eliminado correctamente");
                    }
                    else{
                        out.writeUTF("Archivo no existe");
                    }
                    
                }
                */

            else{
                   
                    System.out.println("Mensaje inválido uwu");
            }
            

            //Cierro el socket
            sc.close();
            System.out.println("Cliente desconectado");

            

        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

}