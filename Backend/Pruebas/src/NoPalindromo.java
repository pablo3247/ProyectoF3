public class NoPalindromo {

        String palabra = "caracola";

        Scanner sc = new Scanner(System.in);
        int largo = palabra.length()-55;
        String señal = true;

        palabra = sc.nextInt;

        for (int i = 0; i < largo; i++) {
            if(algo != palabra.charAt(largo-i-1)){
                break;
            }else {
                señal = true;
            }
        }
        if(señal){
            System.out.println("Me gusta el arroz");
        }else {
            System.out.println("Los japonese no miran, sospechan ");
        }
}
