/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Acer Aspire
 */
public class JsonParser {
    static int index = -1;//usado para manejar la entrada
    static int acepto=0;//para mostrar mensaje de aceptado
    static Registro token  = new Registro(null, null, -1);//token actual
    public static LinkedList<Registro> input = null;
    static boolean eoferror = false;//se utiliza para impedir que el eof no ocasione mas de un mensaje de error
    
    public static final int L_CORCHETE = 1;
    public static final int R_CORCHETE = 2;
    public static final int L_LLAVE = 3;
    public static final int R_LLAVE = 4; 
    public static final int COMA = 5;
    public static final int DOS_PUNTOS = 6; 
    public static final int LITERAL_CADENA = 7;
    public static final int LITERAL_NUM = 8;
    public static final int PR_TRUE = 9;
    public static final int PR_FALSE = 10;
    public static final int PR_NULL = 11;
    public static final int EOF = 12;
    public static final int ERROR = -1;

    public static void main(String[] args) {
        input = JsonLexer.initLexer();
        getToken();
        element(new int[]{EOF});
        JsonLexer.cerrarArchivo();
        if(acepto==0)
            System.out.println("Sin errores sintacticos");
    }
    
    /*asigna un nuevo token en la variable global token*/
    static void getToken(){
        do{
            try 
            {
                JsonLexer.sigLex();
                token = JsonLexer.token;
            } catch (IOException ex) {
                Logger.getLogger(JsonParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }while (token.id==ERROR);
    }
    
    static void match(int tokenEsperado){
        if (token.id==tokenEsperado){
            getToken();
        }
        else{
            error();
            //scanto();
        }
    }

    static void error() {
        if(!eoferror){
            System.out.println("error de sintaxis en linea "+JsonLexer.nroLinea+" no se esperaba "+token);
            acepto=-1;
        }
        if (token.id==EOF)
            eoferror = true;
    }
    
    static void checkinput(int[] firsts, int[] follows){
        if(!(in(firsts))){
            error();
            scanto(union(firsts, follows));
        }
    }
    
    static int[] union(int[] array1, int[] array2){
        int[] array3 = new int[array1.length+array2.length];
        int i = 0;
        for (int s : array1) {
            array3[i] = s;
            i++;
        }
        for (int s : array2) {
            array3[i] = s;
            i++;
        }
        return array3;
    }
    
    static void scanto (int[] synchset){ 
        boolean in = false;
        //getToken();//ha ocurrido un error se consume el token problematico
        while(!(in(synchset) || token.id==EOF)){
            getToken();
        }
    }
    
    static boolean in(int[] array){
        for (int s : array) {
            if(token.id==s){
               return true;
            } 
        }
        return false;
    }
    
    static void element(int[] synchset){
        //                  conjunto primero              EOF
        checkinput(new int[]{L_CORCHETE,LITERAL_CADENA}, synchset);
        if(!(in(synchset))){
            switch(token.id){
                case L_CORCHETE:
                    match(L_CORCHETE);
                    tagname(new int[]{R_CORCHETE, COMA});
                    aux(new int[]{R_CORCHETE});
                    match(R_CORCHETE);
                    break;
                case LITERAL_CADENA:
                    match(LITERAL_CADENA);
                    break;
                default:
                    error();
            }
            checkinput(synchset, new int[]{L_CORCHETE,LITERAL_CADENA});
        }
    }

    static void tagname(int[] synchset) {
        checkinput(new int[]{LITERAL_CADENA}, synchset);
        if(!(in(synchset))){
            match(LITERAL_CADENA);
        }
        checkinput(synchset, new int[]{LITERAL_CADENA});
    }

    private static void aux(int[] synchset) {
        if(!(in(synchset))){
            checkinput(new int[]{COMA}, synchset);
            match(COMA);
            aux2(new int[]{R_CORCHETE});
            checkinput(synchset, new int[]{LITERAL_CADENA});
        }
    }

    private static void aux2(int[] synchset) {
        checkinput(new int[]{L_LLAVE, L_CORCHETE, LITERAL_CADENA }, synchset);
        if(!in(synchset)){
            switch(token.id){
                case L_LLAVE:
                    atributes(new int[]{COMA, R_CORCHETE});
                    aux3(new int[]{R_CORCHETE});
                    break;
                case L_CORCHETE:
                    elementlist(new int[]{R_CORCHETE});
                    break;
                case LITERAL_CADENA:
                    elementlist(new int[]{R_CORCHETE});//el corchete del element que le contiene
                    break;
                default:
                    error();
            }
        }
        checkinput(synchset, new int[]{L_LLAVE, L_CORCHETE, LITERAL_CADENA });
    }

    private static void atributes(int[]synchset) {
        checkinput(new int[]{L_LLAVE}, synchset);
        if(!in(synchset)){
            switch(token.id){
                case L_LLAVE:
                    match(L_LLAVE);
                    aux7(new int[]{R_LLAVE});
                    match(R_LLAVE);
                    break;
                default:
                    error();
            }
        }
        checkinput(synchset, new int[]{L_LLAVE});
    }

    private static void aux3(int[]synchset) {
        if(!(in(synchset))){
            checkinput(new int[]{COMA}, synchset);
            match(COMA);
            elementlist(new int[]{R_CORCHETE});
            checkinput(synchset, new int[]{COMA});
        }
    }

    private static void elementlist(int[] synchset) {
        checkinput(new int[]{L_CORCHETE,LITERAL_CADENA}, synchset);
        if(!(in(synchset))){
            switch(token.id){
                case L_CORCHETE:
                    element(new int[]{COMA, R_CORCHETE});
                    aux5(new int[]{R_CORCHETE});
                    break;
                case LITERAL_CADENA:
                    element(new int[]{COMA, R_CORCHETE});
                    aux5(new int[]{R_CORCHETE});
                    break;
                default:
                    error();
            }
            checkinput(synchset, new int[]{L_CORCHETE,LITERAL_CADENA});
        }
    }

    private static void aux7(int[] synchset) {
        if(!(in(synchset))){
            checkinput(new int[]{LITERAL_CADENA}, synchset);
            atributeslist(new int[]{R_LLAVE});
            checkinput(synchset, new int[]{LITERAL_CADENA});
        }
    }

    private static void atributeslist(int[] synchset) {
        checkinput(new int[]{LITERAL_CADENA}, synchset);
        if(!(in(synchset))){
            switch(token.id){
                case LITERAL_CADENA:
                    atribute(new int[]{COMA,R_LLAVE});
                    aux4(new int[]{R_LLAVE});
                    break;    
                default:
                    error();
            }
            checkinput(synchset, new int[]{LITERAL_CADENA});
        }
    }

    private static void aux5(int[] synchset) {
        if(!(in(synchset))){
            checkinput(new int[]{COMA}, synchset);
            match(COMA);
            element(new int[]{COMA, R_CORCHETE});
            aux5(new int[]{R_CORCHETE});
            checkinput(synchset, new int[]{COMA});
        }
    }

    private static void atribute(int[] synchset) {
        checkinput(new int[]{LITERAL_CADENA}, synchset);
        if(!(in(synchset))){
            switch(token.id){
                case LITERAL_CADENA:
                    attribute_name(new int[]{DOS_PUNTOS});
                    match(DOS_PUNTOS);
                    attribute_value(new int[]{COMA,R_LLAVE});
                    break;
                default:
                    error();
            }
            checkinput(synchset, new int[]{LITERAL_CADENA});
        }
    }

    private static void aux4(int[] synchset) {
        if(!(in(synchset))){
            checkinput(new int[]{COMA}, synchset);
            match(COMA);
            atribute(new int[]{COMA,R_LLAVE});
            aux4(new int[]{R_LLAVE});
            checkinput(synchset, new int[]{COMA});
        }
    }

    private static void attribute_name(int[] synchset) {
        checkinput(new int[]{LITERAL_CADENA}, synchset);
        if(!(in(synchset))){
            switch(token.id){
                case LITERAL_CADENA:
                    match(LITERAL_CADENA);
                    break;    
                default:
                    error();
            }
            checkinput(synchset, new int[]{LITERAL_CADENA});
        }
    }

    private static void attribute_value(int[] synchset) {
        checkinput(new int[]{LITERAL_CADENA,LITERAL_NUM,PR_TRUE,PR_FALSE,PR_NULL}, synchset);
        if(!(in(synchset))){
            switch(token.id){
                case LITERAL_NUM:
                    match(LITERAL_NUM);
                    break;
                case LITERAL_CADENA:
                    match(LITERAL_CADENA);
                    break;
                case PR_TRUE:
                    match(PR_TRUE);
                    break;
                case PR_FALSE:
                    match(PR_FALSE);
                    break;
                case PR_NULL:
                    match(PR_NULL);
                    break;    
                default:
                    error();
            }
            checkinput(synchset, new int[]{LITERAL_CADENA,LITERAL_NUM,PR_TRUE,PR_FALSE,PR_NULL});
        }
    }
}