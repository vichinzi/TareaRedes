import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {

        //Host del servidor
        final String HOST;    //"127.0.0.1"
        System.out.println("Ingrese IP para la conexion");
        
        Scanner scn = new Scanner(System.in); //despues se rehusa para el menu
        HOST = scn.nextLine();


        //Puerto del servidor
        final int PUERTO = 11111;

        try {
			
			
			
            //Creo el socket para conectar con cliente
            Socket sc = new Socket(HOST, PUERTO); //(1) peticion conexión
                        
                        
            String mensaje;  //respuesta de menu, se envia a Server
            
            
            //variables ocupadas en ls
            String ls;  
            Boolean lsss;
            
            //variables usadas para put y get
            String palabra;
            String[] palabras 
            
			boolean flag = true;
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
                        ls = in.readUTF();  //(-><-) lee cada path enviado por el server hasta un mensaje Finisher
                        
                        if (ls.equals("Finisher")){
                            lsss=false;
                        }
                        else{
                           System.out.println(ls); 
                        }
                    }
                    
                    mensaje = in.readUTF(); //despues borrar esto **
                }

                if (mensaje.split(" ")[0].equals("get")) {
                    
                    palabras = mensaje.split(" "); 
                    palabra = palabras[1].replaceAll("\\s",""); 
                    try {
                        new Cliente().recivir(sc, palabra);
                    } catch (Exception ex) {
                        Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (mensaje.split(" ")[0].equals("put")) {
                    DataInputStream in = new DataInputStream(sc.getInputStream());

                    palabra = in.readUTF(); //lee la palabra que le manda el if de put en server
                    //OutputStream outs = sc.getOutputStream();

                    try {
                        new Cliente().enviar(palabra,sc);
                    } catch (Exception ex) {
                        Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                    }    
                }


                else if (mensaje.split(" ")[0].equals("delete")){
					
					//se envia a Server para que elimine el archivo
                    DataInputStream in = new DataInputStream(sc.getInputStream());
                    System.out.println(in.readUTF());
                }
               
            }
            sc.close();

        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


    public void enviar(String palabra, Socket socket) throws Exception {  //funcion usada por el put

        String concatenacion = System.getProperty("user.dir") + "/" + palabra;  //directorio de archivo a enviar
        
        //System.out.println(concatenacion);
        
        File myFile = new File(concatenacion);
        int largo = (int) myFile.length(); //largo de archivo
        String largito = Integer.toString(largo); //same pero en string


        DataOutputStream osss = new DataOutputStream(socket.getOutputStream());
        
        osss.writeUTF(largito); //se manda a server el largo del archivo
        
        DataInputStream issss = new DataInputStream(socket.getInputStream());
        issss.readUTF();
        
        
        if(issss.equals("gracias")){
        	System.out.println("llego el gracias"); //largo recibido por server
        }

        byte[] mybytearray = new byte[largo]; //este arreglo contendra el archivo para ser enviado
        
		//manejadores de archivos para pasarlo a bytes
        FileInputStream fis = new FileInputStream(concatenacion);  
        BufferedInputStream bis = new BufferedInputStream(fis);
        
        //se usara para traspaso de bytes via socket
        BufferedOutputStream outt = new BufferedOutputStream(socket.getOutputStream());

        int current;
        current = bis.read(mybytearray); //se pasa al arrego que se mencionó antes
        
        //se manda archivo por socket
        outt.write(mybytearray,0, current);
        System.out.println("Archivo enviado");
        
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
        largo = isss.readUTF(); //se lee el largo del archivo enviado
        largito = Integer.parseInt(largo); //se castea a entero

        DataOutputStream osss = new DataOutputStream(socket.getOutputStream());
        osss.writeUTF("gracias");  //comunicacion con cliente, listo para la recepción del archivo

        InputStream iss = socket.getInputStream(); //se crea la conexion con server
        
        byte[] mybytearray = new byte[largito]; //arreglo de bytes con largo recibido
        
      
        current = iss.read(mybytearray);  //se traspasa a bytes la informacion recibida
        
        //manejadores de archivo nuevo
        FileOutputStream fos = new FileOutputStream(nombre);  
        BufferedOutputStream bos = new BufferedOutputStream(fos);

		//se crea el archivo recibido, desde los bytes
        bos.write(mybytearray, 0, current); 

        //bos.write(mybytearray, 0, bytesRead);
        //bos.flush();
        System.out.println("Archivo creado");
        bos.close();
        fos.close();
    }
}
