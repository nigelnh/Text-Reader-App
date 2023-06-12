
package cs1.TextReaderApp;

import  cs1.app.*;

public class TextReaderApp
{
    void printTable( int[][] table )
    {
        int numRows = table.length;
        int numCols = table[0].length;
        
        for ( int r = 0; r < numRows; r++ )
        {
            for ( int c = 0; c < numCols; c++ )
            {
                int curValue = table[ r ][ c ];
                System.out.print( curValue + ", " );
            }
            
            System.out.println();
        }
    }
    
    void printArray ( long[] numbers )
    {
        for ( int index = 0; index < numbers.length; index++ )
        {
            long curValue = numbers[ index ];
            
            System.out.print( curValue + ", " );
        }
        
        System.out.println();
    }
    
    int[][] copyImage( int startRow, int startCol, int numRows, int numCols, int[][] bigImage )
    {
        int[][] copy = new int[ numRows ][ numCols ];
        int br = startRow;
        int bc = startCol;
        
        for ( int sr = 0; sr < numRows; sr++ )
        {
            for ( int sc = 0; sc < numCols; sc++ )
            {
                int pixel = bigImage[ br ][ bc ];
                copy[ sr ][ sc ] = pixel;
                
                bc++;
            }
            
            bc = startCol;
            br++;
        }
        
        return copy;
    }
    
    void pasteImage( int startRow, int startCol, int[][] bigImage, int[][] smallImage )
    {
        int br = startRow;
        int bc = startCol;
        
        for ( int sr = 0; sr < smallImage.length; sr++ )
        {
            for ( int sc = 0; sc < smallImage[sr].length; sc++ )
            {
                bigImage[ br ][ bc ] = smallImage[ sr ][ sc ];
                
                bc++;
            }
            
            bc = startCol;
            br++;
        }
    }
    
    int[][] createLogo( String phrase )
    {
        int[][] bigImage = new int[ phrase.length()*25 ][ phrase.length()*23 ];
        int index = 0;
        int sr = 0;
        int sc = 0;
        
        for ( int br = 0; br < bigImage.length; br = br + 25 )
        {
            for ( int bc = 0; bc < bigImage[br].length; bc = bc + 23 )
            {
                if ( br == sr && bc == sc )
                {
                    char letter = phrase.charAt( index );
                    int[][] smallImage = canvas.readImage( letter + ".png" );
                    pasteImage( sr, sc, bigImage, smallImage );
                    
                    sr = sr + 25;
                    sc = sc + 23;
                    index++;
                }
            }           
        }
        
        return bigImage;
    }
    
    long computeDifference( int[][] image1, int[][] image2 )
    {
        long sum = 0;
        int r2 = 0;
        int c2 = 0;
        
        for ( int r1 = 0; r1 < image1.length; r1++ )
        {
            for ( int c1 = 0; c1 < image1[r1].length; c1++ )
            {
                long value =  Math.abs( image1[ r1 ][ c1 ] - image2[ r2 ][ c2 ] );
                sum = sum + value;
                
                c2++;
            }
            
            c2 = 0;
            r2++;
        }
        
        return sum; 
    }
    
    long findMinDifference( int[][] bigImage, int[][] smallImage )
    {
        long minDifference = 0;
        long curMinDifference = Long.MAX_VALUE;
        
        for ( int br = 0; br < bigImage.length - smallImage.length + 1; br++ )
        {
            for ( int bc = 0; bc < bigImage[br].length - smallImage[0].length + 1; bc++ )
            {
                int[][] portionImage = copyImage( br, bc, smallImage.length, smallImage[0].length, bigImage );
                
                minDifference = computeDifference( portionImage, smallImage );
                
                if ( minDifference < curMinDifference )
                {
                    curMinDifference = minDifference;
                }
            }
        }
        
        return curMinDifference;
    }
    
    long[] findLetterScores( int[][] image )
    {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        long[] result = new long[ alphabet.length() ];
        
        for ( int index = 0; index < alphabet.length(); index++ )
        {
            char letter = alphabet.charAt( index );
            int[][] letterImage = canvas.readImage( letter + ".png" );
            
            long minDifference = findMinDifference( image, letterImage );
            
            result[ index ] = minDifference;
        }
        
        return result;
    }
    
    int findMinValueIndex( long[] data )
    {
        int minIndex = 0;
        
        for ( int index = 1; index < data.length; index++ )
        {
            if ( data[ minIndex ] > data[ index ] )
            {
                minIndex = index;
            }
        }
        
        return minIndex;
    }
    
    String findLetters( int[][] image )
    {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        int numObjects = countObjects( image );
        String result = "";
        
        long[] scores = findLetterScores( image );
        
        for ( int object = 0; object < numObjects; object++ )
        {
            int minIndex = findMinValueIndex( scores );  
            char letter = alphabet.charAt( minIndex );
            result = result + letter;
            
            scores[ minIndex ] = Long.MAX_VALUE;
        }
        
        return result;
    }
    
    int countObjects( int[][] image )
    {
        int count = 0;
        
        for ( int r = 1; r < image.length - 1; r++ )
        {
            for ( int c = 1; c < image[r].length - 1; c++ )
            {
                if ( image[ r ][ c ] != 0 )
                {                    
                    if ( image[ r-1 ][ c-1 ] == 0 & image[ r-1 ][ c ] == 0 & image[ r-1 ][ c+1 ] == 0 & image[ r ][ c-1 ] == 0 )
                    {
                        count = count + 1;
                    }
                    else if ( image[ r-1 ][ c ] == 0 && image[ r-1 ][ c+1 ] != 0 && image[ r ][ c-1 ] != 0 )
                    {
                        count = count - 1;
                    }
                    else if ( image[ r-1 ][ c-1 ] != 0 && image[ r-1 ][ c ] == 0 && image[ r-1 ][ c+1 ] != 0 )
                    {
                        count = count - 1;
                    }
                }
            }
        }
        
        return count;
    }
    
    void traceContour( int startRow, int startCol, int[][] image )
    {
        int curRow = startRow; 
        int curCol = startCol; 
        
        int dr = 0;
        int dc = 1;
        
        do 
        {
            if ( image[ curRow ][ curCol ] == 0 )
            {
                int curDr = dr;
                dr = dc;
                dc = -curDr;
            }
            else 
            {
                image[ curRow ][ curCol ] = 0x00FF0000;
                
                int curDr = dr;
                dr = -dc;
                dc = curDr;   
            }
            
            curRow = curRow + dr;
            curCol = curCol + dc;
        }
        
        while ( curRow != startRow || curCol != startCol );    
    }
    
    void traceAllContours( int[][] image )
    {
        for ( int r = 1; r < image.length - 1; r++ )
        {
            for ( int c = 1; c < image[r].length - 1; c++ )
            {
                if ( image[ r - 1 ][ c - 1 ] == 0 )
                {
                    traceContour( r, c, image );
                }
            }
        }
    }
    
    void addNoise( int[][] image, int level )
    {
        for ( int r = 1; r < image.length - 1; r++ )
        {
            for ( int c = 1; c < image[r].length -1; c++ )
            {
                if ( image[ r ][ c ] != 0 && image[ r-1 ][ c ] != 0 && image[ r ][ c+1 ] != 0 && image[ r+1 ][ c ] != 0 && image[ r ][ c-1 ] == 0 )
                {
                    int ranNum = canvas.getRandomInt( 0, 100 );
                    
                    if ( ranNum < level )
                    {
                        image[ r ][ c ] = 0;
                    }
                }
            }
        }
    }
    
    void runTextApp()
    {
        int[][] mainImage = createLogo( "glat" );
        String choice = "";
        
        while ( choice.equals( "Quit" ) == false )
        {
            canvas.drawImage( canvas.getWidth()/2, canvas.getHeight()/2, mainImage );                
            Touch touch = canvas.waitForTouch();
            
            canvas.clear();
            choice = canvas.readSelection("OPTIONS", "Load Image", "Create Logo", "Count Regions", "Trace Contours", "Transcribe Text", "Add Noise", "Quit");
            
            if ( choice.equals("Load Image") == true )
            {
                String image = canvas.readString( "Image Loaded" );
                mainImage = canvas.readImage( image + ".png" );
            }
            
            if ( choice.equals( "Create Logo" ) == true )
            {
                String logo = canvas.readString( "Logo Created" );
                int[][] logoCreated = createLogo( logo );
                mainImage = logoCreated;
            }
            
            if ( choice.equals( "Count Regions" ) == true )
            {
                int numRegions = countObjects( mainImage );
                canvas.drawText( canvas.getWidth()/2, canvas.getHeight()/7, "Number of objects: " + numRegions, "green" );
            }
            
            if ( choice.equals( "Trace Contours" ) == true )
            {
                traceAllContours( mainImage );
            }
            
            if ( choice.equals( "Transcribe Text" ) == true )
            {
                String text = findLetters( mainImage );
                canvas.drawText( canvas.getWidth()/2, canvas.getHeight()/7, "The letters are: " + text, "green" );
            }
            
            if ( choice.equals( "Add Noise" ) == true )
            {
                int level = canvas.readInt( "Noise Level" );
                addNoise( mainImage, level );
            }
        }
    }
     
    public void run()
    {
//        int[][] image = { { 10, 11, 12, 13 },{ 14, 15, 16, 17 },{ 18, 19, 20, 21 } };
//
//        int[][] image1 = copyImage( 0, 0, 2, 2, image );
//        printTable( image1 );
//        
//        int[][] image2 = copyImage( 1, 2, 2, 2, image );
//        printTable( image2 );  
//        
//        int[][] image3 = copyImage( 0, 0, 3, 4, image );
//        printTable( image3 ); 
//        
//        int[][] scene = canvas.readImage( "scene1.png" );
//        int[][] sun = copyImage( 10, 6, 154, 171, scene );
//        canvas.drawImage( 159, 265, sun );
//        
//        int[][] scene = canvas.readImage( "scene1.png" );
//        int[][] cloud = copyImage( 121, 185, 81, 109, scene );
//        canvas.drawImage( 159, 265, cloud );
//        
//        int[][] scene = canvas.readImage( "scene1.png" );
//        int[][] smiley = copyImage( 260, 181, 115, 115, scene );
//        canvas.drawImage( 159, 265, smiley );
//        
//        int[][] letterQ = canvas.readImage( "q.png" );
//        int[][] copyQ = copyImage( 0, 0, 25, 23, letterQ );
//        canvas.drawImage( 159, 265, copyQ );
//        
//        int[][] image1 = { { 10, 11, 12, 13 }, { 14, 15, 16, 17 }, { 18, 19, 20, 21 } };
//
//        int[][] image2 = { { 30, 31 }, { 32, 33 } };
//
//        pasteImage( 0, 0, image1, image2 );
//        printTable( image1 );
//        
//        pasteImage( 1, 2, image1, image2 );
//        printTable( image1 );
//        
//        int[][] scene = canvas.readImage( "scene1.png" );
//        int[][] letterS = canvas.readImage( "s.png" );
//        int[][] letterC = canvas.readImage( "c.png" );
//        pasteImage( 74, 80, scene, letterS );
//        pasteImage( 149, 227, scene, letterC );
//        canvas.drawImage( 159, 265, scene );
//        
//        int[][] letterS = canvas.readImage( "s.png" );
//        int[][] letterC = canvas.readImage( "c.png" );
//        pasteImage( 0, 0, letterC, letterS );
//        canvas.drawImage( 159, 265, letterC );
//        
//        int[][] gburgImage = createLogo( "gbcs" );
//        canvas.drawImage( 159, 265, gburgImage );
//        
//        int[][] hiImage = createLogo( "hi" );
//        canvas.drawImage( 159, 265, hiImage );
//        
//        int[][] catImage = createLogo( "cat" );
//        canvas.drawImage( 159, 265, catImage );
//        
//        int[][] zImage = createLogo( "z" );
//        canvas.drawImage( 159, 265, zImage );
//          
//        int[][] image1 = { { 1, 2, 3, 4 }, { 5, 6, 7, 8 } };
//          
//        int[][] image2 = { { 8, 7, 6, 5 }, { 4, 3, 2, 1 } };
//           
//        long diff = computeDifference( image1, image2 );
//        System.out.println( "difference is: " + diff );
//        
//        int[][] letterQ = canvas.readImage( "q.png" );
//        int[][] letterX = canvas.readImage( "x.png" );
//        int[][] letterO = canvas.readImage( "o.png" );
//
//        long diff = computeDifference( letterQ, letterQ );
//        System.out.println( "Q - Q is: " + diff );             
//        
//        diff = computeDifference( letterX, letterQ );
//        System.out.println( "X - Q is: " + diff );              
//
//        diff = computeDifference( letterQ, letterX );
//        System.out.println( "Q - X is: " + diff );              // displays Q - X is 3741318945
//
//        diff = computeDifference( letterO, letterQ );
//        System.out.println( "O - Q is: " + diff );              // displays O - Q is 452984805
//
//        diff = computeDifference( letterQ, letterO );
//        System.out.println( "Q - O is: " + diff );              // displays Q - O is 452984805
//               
//        int[][] image = { { 10, 11, 12, 13 }, { 14, 15, 16, 17 }, { 18, 19, 20, 21 } };
//    
//        int[][] image1 = { { 16, 14 }, { 17, 20 } };
//
//        int[][] image2 = { { 16, 17 }, { 21, 20 } };
//
//        long diff = findMinDifference( image, image1 );
//        System.out.println( "min difference is: " + diff );     // displays 5
//
//        diff = findMinDifference( image, image2 );
//        System.out.println( "min difference is: " + diff );     // displays 2
//              
//       
//        int[][] quartzImage = canvas.readImage( "quartz1.png" );
//
//        int[][] qImage = canvas.readImage( "q.png" );
//        long diff = findMinDifference( quartzImage, qImage );
//        System.out.println( "min( Q - QUARTZ ) is: " + diff );         // displays 0 (since Q is in QUARTZ)
//
//        int[][] zImage = canvas.readImage( "z.png" );
//        diff = findMinDifference( quartzImage, zImage );
//        System.out.println( "min( Z - QUARTZ ) is: " + diff );         // displays 0 (since Z is in QUARTZ)
//
//        int[][] oImage = canvas.readImage( "o.png" );
//        diff = findMinDifference( quartzImage, oImage );   
//        System.out.println( "min( O - QUARTZ ) is: " + diff );         // displays 452984805
//
//        int[][] yImage = canvas.readImage( "y.png" );
//        diff = findMinDifference( quartzImage, yImage );      
//        System.out.println( "min( Y - QUARTZ ) is: " + diff );         // displays 1358954415
//        int[][] wImage = canvas.readImage( "w.png" );
//        diff = findMinDifference( quartzImage, wImage );     
//        System.out.println( "min( W - QUARTZ ) is: " + diff );         // displays 2499805035
//        
//        int[][] taxiImage = canvas.readImage( "taxi1.png" );
//        long[] scores = findLetterScores( taxiImage );
//        printArray( scores );
//          
//        int[][] taxiImage = canvas.readImage( "taxi1.png" );
//
//        String letters = findLetters( taxiImage, 4 );
//        System.out.println( "top 4 letters in taxi.png are " + letters );    // displays AITX
//        
//        letters = findLetters( taxiImage, 3 );             
//        System.out.println( "top 3 letters in taxi.png are " + letters );    // displays AIT
//
//        letters = findLetters( taxiImage, 1 );             
//        System.out.println( "top 1 letters in taxi.png are " + letters );    // displays A
//        
//        int[][] taxiTiltedImage = canvas.readImage( "taxi2.png" );
//
//        letters = findLetters( taxiTiltedImage, 4 );             
//        System.out.println( "top 4 letters in taxi2.png are " + letters );    // displays tiaz
//        
//        int[][] quartzImage = canvas.readImage( "quartz1.png" );
//
//        String letters = findLetters( quartzImage, 6 );             
//        System.out.println( "top 6 letters in quartz1.png are " + letters );    // displays AQRTUZ
//
//        letters = findLetters( quartzImage, 5 );
//        System.out.println( "top 5 letters in quartz1.png are " + letters );    // displays AQRTU
//
//        letters = findLetters( quartzImage, 1 );
//        System.out.println( "top 1 letters in quartz1.png are " + letters );    // displays A
//
//        int[][] quartzTiltedImage = canvas.readImage( "quartz2.png" );
//
//        letters = findLetters( quartzTiltedImage, 6 );
//        System.out.println( "top 6 letters in quartz2.png are " + letters );    // displays tzaqiu
//        
//        int[][] scene = canvas.readImage( "scene1.png" );
//        int count = countObjects( scene );
//        System.out.println("#objects in scene = " + count);
//        
//        int[][] wImage = canvas.readImage( "w.png" );
//        int count = countObjects( wImage );
//        System.out.println("#objects in image of W = " + count);
//               
//        int[][] image = { { 0, 0, 0, 0, 0 }, { 0, 255, 255, 255, 0 }, { 0, 255, 255, 255, 0 }, { 0, 255, 255, 255, 0 }, { 0, 0, 0, 0, 0 } };
//          
//        traceContour( 2, 1, image );
//        printTable( image );
//        
//        int[][] scene = canvas.readImage( "scene1.png" );
//        traceContour( 255, 70, scene );
//        canvas.drawImage( 159, 265, scene );
//                
//        int[][] scene = canvas.readImage( "scene1.png" );
//        traceContour( 284, 191, scene );
//        canvas.drawImage( 159, 265, scene );
//        
//        int[][] scene = canvas.readImage( "scene1.png" );
//        traceContour( 281, 33, scene );
//        canvas.drawImage( 159, 265, scene );
//        
//        int[][] letterA = canvas.readImage( "a.png" );
//        traceContour( 2, 9, letterA );
//        canvas.drawImage( 159, 265, letterA );
//               
//        int[][] image = { { 0,   0,   0,   0,   0,   0,   0 }, { 0, 255, 255,   0,   0,   0,   0 }, { 0, 255, 255,   0, 255, 255,   0 }, { 0,   0,   0,   0, 255, 255,   0 }, { 0,   0,   0,   0,   0,   0,   0 } };
//          
//        traceAllContours( image );
//        printTable( image );
//        
//        int[][] scene = canvas.readImage( "scene1.png" );
//        traceAllContours( scene );
//        canvas.drawImage( 159, 265, scene );
//        
//        int[][] letterQ = canvas.readImage( "q.png" );
//        traceAllContours( letterQ );
//        canvas.drawImage( 159, 265, letterQ );
//        
//        int[][] image = canvas.readImage( "scene1.png" );
//        addNoise( image, 1 );
//        canvas.drawImage ( 159, 265, image );
//        
        runTextApp();
    }
}