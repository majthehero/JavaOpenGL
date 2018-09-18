package maj;

public class Util {

    public static int log2( int num ) // returns 0 for num=0
    {
        /**
         *  Calculates logarithm base 2 (number).
         * From https://stackoverflow.com/questions/3305059/how-do-you-calculate-log-base-2-in-java-for-integers
         */
        int log = 0;
        if( ( num & 0xffff0000 ) != 0 ) { num >>>= 16; log = 16; }
        if( num >= 256 ) { num >>>= 8; log += 8; }
        if( num >= 16  ) { num >>>= 4; log += 4; }
        if( num >= 4   ) { num >>>= 2; log += 2; }
        return log + ( num >>> 1 );
    }

}
