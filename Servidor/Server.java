
import java.net.ServerSocket;
import java.util.Scanner;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import java.io.*;
import java.sql.Timestamp;
import java.util.concurrent.ThreadLocalRandom;

public class Server {
    public static void main(String[] args) {
        try {

            File log = new File("log.txt");
            String header = "DATETIME\t\tEVENT\t\tDESCRIPTION";
            BufferedWriter hdr = new BufferedWriter(new FileWriter(log,true));
            hdr.write(header);
            hdr.close();
            final int PUERTO = 11111;

            System.out.println("alo");

            ServerSocket server = new ServerSocket(PUERTO);

            System.out.println("Servidor iniciado");
            while (true) {                                              
                Socket socketpropio = server.accept();                                         
                new ThreadSocket(socketpropio);
                System.out.println("Cliente conectado");
            }
        } catch (Exception e) {
        }
    }

    public boolean estaenarr(String [] arr,int n ,String element){
        for (int i=0;i<n;i++) {
            if (element.equals(arr[i])) {
                return true;
            }
        }
        return false;   
    }

    public void lss(DataOutputStream oe,String socketIP,String []ips,int n_ip) throws Exception{
        
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String loguear = "\n"+time+"\tcommand\t\t"+socketIP+" ls\n";
        File log = new File("log.txt");
        BufferedWriter w;
        w = new BufferedWriter(new FileWriter(log,true));
        w.newLine();
        w.write(loguear);
        w.close();

        String ip2 [] =new String [n_ip] ;
        int j = 0;
        for (int i=0; i<n_ip;i++){//agrega a la lista ip2 los que logran conectarse de ips, j es tamaño final
            try{
                Socket soc = new Socket(ips[i], 11111);
                //despues de hs
                System.out.println("conexion con: " + ips[i]);
                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                dos.writeUTF("ls");


                ip2[j] = ips[i];
                j++; 
                soc.close();


            }
            catch(Exception e){
                
                continue;
            }
        }


        File dir = new File("INDEX.txt");
        BufferedReader br = new BufferedReader(new FileReader(dir));
        String st, st2;
        boolean ayuda;
        while((st = br.readLine()) != null){
            st = st.split(" ")[1];
            File dir2 =new File(st);
            BufferedReader br2 = new BufferedReader(new FileReader(dir2));
            boolean flaggy = true;
            System.out.println("st: " +st);
            while((st2 = br2.readLine()) != null){ //recorriendo miniindex
                System.out.println(st2);
                ayuda = estaenarr(ip2,j,st2);
                if (!ayuda){//si cualquier ip de éste no esta en lista de ips utiles no sirve
                    flaggy = false;

                }

            }
            System.out.println(flaggy);
            if (flaggy){
                oe.writeUTF(st);
            }
            
            br2.close();
        }
        br.close();
        


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

    public void recibirparanenvio(Socket socket, String name, String[] ipes, File file, int n_ip, int puer,String nombresinext,String jpg) throws Exception {

        String largo;
        int largito;
        int bytesRead;
        int current = 0;
        String nombre = name;
        int contador = 1;//I like to name parts from 001, 002, 003, ...
        //you can change it to 0 if you want 000, 001, ...

        DataInputStream isss = new DataInputStream(socket.getInputStream());
        largo = isss.readUTF();
        largito = Integer.parseInt(largo);
        System.out.println(largito);

        DataOutputStream osss = new DataOutputStream(socket.getOutputStream());
        osss.writeUTF("gracias");

        DataInputStream iss = new DataInputStream(socket.getInputStream());

        byte[] mybytearray = new byte[largito];
        //FileOutputStream fos = new FileOutputStream(nombre);
        //BufferedOutputStream bos = new BufferedOutputStream(fos);

        iss.readFully(mybytearray); //Aqui leo el archivo desde el cliente entrito y lo escribo a mybytearray

        //dividir en las maquinas correspondientes

        int largodenvio = 1024 * 64;// 64KB
        //byte[] buffer = new byte[largodenvio];

        //try-with-resources to ensure closing stream
        int largobase = 0;
        int nro_index = 0;
        String str_largo;
        int tento;
        while (largito > 0) {//mientras queden bytes por enviar

            int randomIPindex = ThreadLocalRandom.current().nextInt(0, n_ip);
            String ipcita = ipes[randomIPindex];//obtengo la ip donde quedara el pedazito
            BufferedWriter escribirr = new BufferedWriter(new FileWriter(file,true));//escribo la ip en el index
            escribirr.write(ipcita);
            System.out.println(ipcita);
            escribirr.close();

            Socket socketmaquina = new Socket(ipcita, puer); //me conecto a la maquina
            DataOutputStream outps = new DataOutputStream(socketmaquina.getOutputStream());
            String index_stringeado = Integer.toString(nro_index);

            //IMPORTANTE: FORMATO DE ENVIO ES: Metodo/nombre_archivo_a_crear_en_maquina.txt
            String nombrearchivo = "put " + jpg;
            outps.writeUTF(nombrearchivo); //escribo el nombre que tendra el archivo en la maquina (haya lo guardo)

            DataInputStream inpst = new DataInputStream(socketmaquina.getInputStream());
            System.out.println(inpst.readUTF()); //confirmacion de que se recivio el nombre
            str_largo = Integer.toString(largito);

            DataOutputStream outputito = new DataOutputStream(socketmaquina.getOutputStream());
            outputito.writeUTF(str_largo);


            DataInputStream inputito = new DataInputStream(socketmaquina.getInputStream());
            String algo = inputito.readUTF();
            System.out.println("El largo es: "+ algo);//confirmacion de que se recivio el largo

            //falta el handshake!!!

            BufferedOutputStream outmaquina = new BufferedOutputStream(socketmaquina.getOutputStream());
            System.out.println(largito);

            if (largito > largodenvio) {
                outmaquina.write(mybytearray, largobase, largodenvio);
            }else{//si es el pedazo sobrante
                outmaquina.write(mybytearray, largobase, largito);
            }

            largobase = largobase + largodenvio;
            largito = largito - largodenvio;
            nro_index = nro_index + 1;
            //leo la confirmacion de la maquina
            socketmaquina.close();
            //out.write(buffer, 0, largodenvio);
            
        }


        //bos.write(mybytearray, 0, largito);
        //System.out.println("Archivo creado");
        //bos.close();
        //fos.close();
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
            Timestamp time = new Timestamp(System.currentTimeMillis());
            String loguear = "\n"+time+"\tconnection\t"+socketIP+"conexion entrante\n";
            File log = new File("log.txt");
            BufferedWriter w;
            w = new BufferedWriter(new FileWriter(log,true));
            w.newLine();
            w.write(loguear);
            w.close();

            File ipfile = new File("ip.txt");
            BufferedReader br = new BufferedReader(new FileReader(ipfile));
            int n_ip = Integer.parseInt(br.readLine());
            String[] ips = new String[n_ip];
            
            for (int i=0;i<n_ip;i++){
                ips[i] = br.readLine();

            }

            br.close();

            File file = new File("INDEX.txt");
            if(file.createNewFile())
                System.out.println("Se creo fichero INDEX");


            


            DataInputStream in;
            File filemanager;
            System.out.println("procesing request");
            String mensaje;


            in = new DataInputStream(sc.getInputStream());
            Boolean flag = true;

            while(flag){

                //Leo el mensaje que me envia el cliente
                mensaje = in.readUTF();


                if (mensaje.equals("salir")){

                    DataOutputStream out = new DataOutputStream(sc.getOutputStream());
                    out.writeUTF("salir");
                    flag=false;

                }

                else if(mensaje.equals("ls")){

                    DataOutputStream out = new DataOutputStream(sc.getOutputStream());
                    try {
                        new Server().lss(out,socketIP,ips,n_ip);
                    } catch (Exception ex) {
                        Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                /*cuando llega una peticion get "archivo" se debe:
                    -recorrer el archivo INDEX para verificar que exista el index de archivo
                    -ir al index de archivo ("archivo.txt") y recorrerlo
                    -Por cada linea del index, tomar la maquina e ir a buscar el pedazo de archivo, y sumarlo en una variable
                    -finalmente devolverlo al cliente
                */

                else if(mensaje.split(" ")[0].equals("get")) { 



                    String[] palabras = mensaje.split(" ");
                    String palabra = palabras[1].replaceAll("\\s","");
                    System.out.println(palabra);
                    try {
                        new Server().enviar(palabra,sc);
                    } catch (Exception ex) {
                        Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                /*Cuando llega una peticion put "archivo" se debe:
                    -Verificar si existe INDEX.txt, sino, crearlo
                    -crear el archivo "archivo.txt" que tendra donde se encuentran los pedazos
                    -dividir cada pedazo con random, escribirlo en "archivo.txt" y enviarlo a su respectiva maquina
                    -luego de enviar todos los pedazos, escribir en el INDEX que el archivo tiene su index llamado "archivo.txt"

                */

                else if(mensaje.split(" ")[0].equals("put")) {

                    //Recivimos put "archivo" y obtenemos la palabra
                    String[] palabras = mensaje.split(" ");
                    
                    String palabra = palabras[1]; //algo.jpg

                    String[] sinexts = new String[2];
                    sinexts = palabra.split("\\.");
                    System.out.println();
                    
                    String nombre = sinexts[0] + ".txt";

                    //Creamos el index palabra.txt
                    File archivo = new File(nombre);
                    if (archivo.createNewFile()) {  
                        System.out.println("Se creo el archivo" + nombre);
                    }

 
                    //Redundancia necesaria
                    DataOutputStream out = new DataOutputStream(sc.getOutputStream());
                    out.writeUTF(palabra);
                    
                    try {
                        new Server().recibirparanenvio(sc,nombre,ips,archivo,n_ip,11111,sinexts[0],palabra);

                        String adesong = palabra + " " + nombre + "\n";
                        BufferedWriter indx = new BufferedWriter(new FileWriter(file,true));//escribo la ip en el index
                        indx.write(adesong);
                        indx.close();

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

                else{
                    DataOutputStream out = new DataOutputStream(sc.getOutputStream());
                    System.out.println("Mensaje inválido uwu");
                }
            }

            //Cierro el socket
            sc.close();
            System.out.println("Cliente desconectado");

            

        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

}