/*
 * SampleFrame.java
 *
 */
package com.amaggioni.AngeloJavaClient;

import com.amaggioni.start.ManageUserRequests;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import javax.swing.JPanel;


class AngeloTradingWorkstation extends JFrame
{

   
    
    private IBTextPanel m_tickers = new IBTextPanel("Market and Historical Data", false);
    private IBTextPanel m_TWS = new IBTextPanel("TWS Server Responses", false);
    private IBTextPanel m_errors = new IBTextPanel("Errors and Messages", false);
    

    AngeloTradingWorkstation()
    {
        JPanel scrollingWindowDisplayPanel = new JPanel(new GridLayout(0, 1));
        scrollingWindowDisplayPanel.add(m_tickers);
        scrollingWindowDisplayPanel.add(m_TWS);
        scrollingWindowDisplayPanel.add(m_errors);

        JPanel buttonPanel = createButtonPanel();

        getContentPane().add(scrollingWindowDisplayPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.EAST);
        setSize(600, 700);
        setTitle("Angelo Trading Workstation Version 1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel createButtonPanel()
    {
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        JButton butConnect = new JButton("Connect");
        butConnect.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
             //   new ManageUserRequests().runx();
                //onConnect();
            }
        });

        JButton butMktData = new JButton("Req Mkt Data");
        butMktData.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                onReqMktData();
            }
        });


        JButton butClear = new JButton("Clear");
        butClear.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                onClear();
            }
        });
        
        JButton butClose = new JButton("Close");
        butClose.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                onClose();
            }
        });


        buttonPanel.add(new JPanel());
        buttonPanel.add(butConnect);


        buttonPanel.add(new JPanel());
        buttonPanel.add(butMktData);

        buttonPanel.add(new JPanel());


        buttonPanel.add(new JPanel());


        buttonPanel.add(new JPanel());
        buttonPanel.add(butClear);
        buttonPanel.add(butClose);

        return buttonPanel;
    }

    void onConnect()
    {

        this.m_TWS.add("Connection test message 1");
        this.m_errors.add("Connection test message 2");
        this.m_tickers.add("Connection test message 3");
    }

    void onReqMktData()
    {
        this.m_TWS.add("onReqMktData test message 1");
        this.m_errors.add("onReqMktData test message 2");
        this.m_tickers.add("onReqMktData test message 3");
    }

    void onClear()
    {
        m_tickers.clear();
        m_TWS.clear();
        m_errors.clear();
    }

    void onClose()
    {
        System.exit(1);
    }

    
    public void connectionClosed()
    {
       
        Main.inform(this, "");
    }
}
