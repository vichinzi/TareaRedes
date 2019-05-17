import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;

public class Cliente {

    public static void main(String[] args) {

        //Host del servidor
        final String HOST; //"127.0.0.1"
        System.out.println("Ingrese el puerto para la conexion");
        Scanner scn = new Scanner(System.in);
        HOST = scn.nextLine();

        //Puerto del servidor
        final int PUERTO = 11111;

        try {
            //Creo el socket para conectarme con el cliente
            Socket sc = new Socket(HOST, PUERTO); //(1)peticion conexión
                        

            boolean flag = true;
            String mensaje;
            String ls;
            Boolean lsss;

            while(flag){

                //Menu
                System.out.println(" ");
                System.out.println("-----------------------------------");
                System.out.println("+Menu:");
                System.out.println("-ls");
                System.out.println("-get nombre_archivo");
                System.out.println("-put nombre_archivo");
                System.out.println("-delete nombre_archivo");
                System.out.println("-salir");
                System.out.println("-----------------------------------");
                System.out.println("+Ingrese opcion:");
                System.out.println(" ");
                mensaje = scn.nextLine();
                DataOutputStream out = new DataOutputStream(sc.getOutputStream());
                out.writeUTF(mensaje); //le manda el mensaje al servidor por el socket sc ----> (3) ida al server


                if ((mensaje.equals("salir"))){
                    flag = false;
                }

                if (mensaje.equals("ls")){
                    DataInputStream in = new DataInputStream(sc.getInputStream());
                    lsss = true;
                    while(lsss){
                        ls = in.readUTF();  //(-><-) lee cada path enviado por el server
                        
                        if (ls.equals("Finisher")){
                            lsss=false;
                        }
                        else{
                           System.out.println(ls); 
                        }
                    }
                    mensaje = in.readUTF();
                }

                if (mensaje.split(" ")[0].equals("get")) {
                    //InputStream in = sc.getInputStream();
                    String[] palabras = mensaje.split(" ");
                    String palabra = palabras[1].replaceAll("\\s",""); //ble.txt por ejemplo
                    try {
                        new Cliente().recivir(sc, palabra);
                    } catch (Exception ex) {
                        Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (mensaje.split(" ")[0].equals("put")) {
                    DataInputStream in = new DataInputStream(sc.getInputStream());

                    String palabra = in.readUTF(); //lee la palabra que le manda el if de put en server
                    //OutputStream outs = sc.getOutputStream();

                    try {
                        new Cliente().enviar(palabra,sc);
                    } catch (Exception ex) {
                        Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                    }    
                }


                else if (mensaje.split(" ")[0].equals("delete")){
                    DataInputStream in = new DataInputStream(sc.getInputStream());
                    System.out.println(in.readUTF());
                }
               
            }
            sc.close();

        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }


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

        current = iss.read(mybytearray); //nunca esta leyendo el -1 porqueeee!!!

        bos.write(mybytearray, 0, current);

        //bos.write(mybytearray, 0, bytesRead);
        //bos.flush();
        System.out.println("Archivo creado");
        bos.close();
        fos.close();
    }
}