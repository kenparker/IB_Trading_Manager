package com.amaggioni.start;


/**
 *
 * @author magang
 */
public class Main extends Thread
{

    private ManageUserRequests underlyingTicketData = null;
    //private static String[] argsx;
     

    public Main(String[] args)
    {
                this.underlyingTicketData = new ManageUserRequests(args);
    }

    public static void main(String[] args)
    {

        //argsx = args;
        new Main(args).start();



    }

    @Override
    public void run()
    {
        

            underlyingTicketData.runx();

    }

    

    

    

    
}

