package com.company;
/**
 *
 * @author YASH KOLADIYA
 * @dateCreated 11 Sep, 2021;
 * --> Network Headers Information Decoder...
 */

/**
 *For Any Header it has three types...
 * 1 --> Ethernet header (48-48-16) = 112
 * 2 --> Network Header  (4-4-6-2-16-16-2-14-8-8-16-32-32-32) = 192
 * 3 --> TCP/Transport Header (16-16-32-32-4-6-1-1-1-1-1-1-16) = 128
 *
 * Total Length = 432
 */

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

//==============================================================================EthernetHeader class========================
class EthernetHeader
{
    //==========================================================================Data Members================================
    //for storing data stream...
    protected String EthDataStream;
    //for storing reference size for each feild in ethernet header...
    protected final int EthHeaderSize[] = {112,48,48,16};//at 0th index the total length is mentioned...
    //for storing tokens for printing purposes...
    protected final String EthHeaderTokens[] = {"Ethernet Header : ","Destination MAC address : ","Source MAC address : ","Type : "};
    //for storing actual bit stream according to individual tokens...
    protected String EthHeaderInfo[];
    private final Pattern dataPattern = Pattern.compile("[^0-1]");
    protected Matcher dataMatcher = null;

    //==========================================================================Construcors=================================
    //default constructor..
    EthernetHeader()
    {
        //by default the data stream is set to null...
        this.EthDataStream = null;
        //init. HeaderInfo array here...
        this.EthHeaderInfo = new String[4];
    }

    //parameterised constructor...
    EthernetHeader(String DataStream)
    {
        //init. the DataStream here...given by user
        this.EthDataStream = DataStream;
        this.EthHeaderInfo = new String[4];
        this.dataMatcher = this.dataPattern.matcher(this.EthDataStream);
    }

    //copy constructor...
    EthernetHeader(EthernetHeader AnotherEtheHead)
    {
        this.EthDataStream = AnotherEtheHead.EthDataStream;
        this.EthHeaderInfo = new String[4];
        this.dataMatcher = this.dataPattern.matcher(this.EthDataStream);
    }

    //==========================================================================Setter/Getter===============================
    public void ReceiveEth(String Stream)
    {
        this.EthDataStream = Stream;
    }

    public String SendEth()
    {
        return this.EthDataStream;
    }

    //==========================================================================Methods======================================

    private boolean ValidateHeader()
    {
        //matching with expected result...
        this.dataMatcher = this.dataPattern.matcher(this.EthDataStream);
        //checking for length...
        if(this.EthDataStream.length() < 112)
        {
            System.out.println("#--> Error Reported By Programmer : Your Ethernet Data Stream Might Have Been Corrupted!");
            return false;
        }
        //checking fro pattern defect...
        else if(this.dataMatcher.find())
        {
            System.out.println("#--> Error Reported By Programmer : Your Ethernet Data Stream Contains Some Illegal Bits.");
            return false;
        }
        else if(this.EthDataStream.length() > 112)
        {
            System.out.println("--> Your Given Stream Is Larger Than Required, Rest Data Will Lost! Because There No Other Layer Coded On Top Of Ethernet!");
            return true;
        }
        //returning true...if no error found...
        else
        {
            return true;
        }
    }

    public void SeparateEthHeader()
    {
        this.SeparateEthHeader(false);
    }

    private boolean SeparateEthHeader(boolean DoReturn)
    {
        if(this.ValidateHeader())
        {
            try
            {
                //for keep traking of index...
                int LastCounted = 0;
                //grabbing ethernet header from data stream...
                this.EthHeaderInfo[0] = this.EthDataStream.substring(LastCounted, LastCounted+this.EthHeaderSize[0]);

                //looping through "HeaderInfo"...
                for(int i=1;i<this.EthHeaderSize.length;i++)
                {
                    //splitting each ethernet header into respective feilds...
                    this.EthHeaderInfo[i] = this.EthHeaderInfo[0].substring(LastCounted, LastCounted+this.EthHeaderSize[i]);
                    LastCounted += this.EthHeaderSize[i];
                }
            }
            catch(Exception e)
            {
                System.out.println("#--> Error Reported By JAVA : "+e);
            }

            if(DoReturn)
            {
                System.out.println("--> Ethernet Data Successfully Seprated!");
            }
            return true;
        }
        else
        {
            System.out.println("#--> Error Reported By Programmer : Your Data Stream For Ethernet Header Is Not Valid!");
            if(DoReturn)
            {
                System.out.println("--> Ethernet Data Is Not In State Of Seprable!");
            }
            return false;
        }
    }

    void ConvertEthToRedable()
    {
        if(this.SeparateEthHeader(false))
        {
            try
            {
                int LastCounted = 0;
                String GrabbedValue;
                String FinalValue = new String();

                for(int i=1;i<this.EthHeaderInfo.length;i++)
                {
                    for(int j=1;j<=(this.EthHeaderSize[i]/4);j++)
                    {
                        GrabbedValue = this.EthHeaderInfo[i].substring(LastCounted, LastCounted+4);
                        LastCounted+=4;
                        if(j==0)
                        {
                            FinalValue = Integer.toHexString(Integer.parseInt(GrabbedValue, 2));
                        }
                        else
                        {
                            FinalValue = FinalValue.concat(Integer.toHexString(Integer.parseInt(GrabbedValue, 2)));
                        }
                    }
                    this.EthHeaderInfo[i] = FinalValue;
                    LastCounted = 0;
                    FinalValue = new String();
                }
            }
            catch(Exception e)
            {
                System.out.println("#--> Error Reported By JAVA : "+e);
            }
        }
        else
        {
            System.out.println("#--> Error Reported By Programmer : Cannot Perform 'ConvertToRedable' Operation For Ethernet Header!");
        }
    }

    private void PrintArray()
    {
        for(int i=1;i<this.EthHeaderTokens.length;i++)
        {
            System.out.println(this.EthHeaderTokens[i]+" "+this.EthHeaderInfo[i]);
        }
    }

    private void AdjustEthFeilds()
    {
        int LastCounted = 0;
        String finalStr = new String();
        for(int i=1;i<3;i++)
        {
            for(int j=1;j<7;j++)
            {
                if(j==1)
                {
                    finalStr = this.EthHeaderInfo[i].substring(LastCounted,LastCounted+2);
                }
                else
                {
                    finalStr = finalStr.concat(":"+this.EthHeaderInfo[i].substring(LastCounted,LastCounted+2));
                }
                LastCounted += 2;
            }
            this.EthHeaderInfo[i] = finalStr;
            LastCounted = 0;
            finalStr = new String();
        }
    }

    void RevealEthHeaderInfo()
    {
        this.RevealEthHeaderInfo(false);
    }

    public void RevealEthHeaderInfo(boolean Adjust)
    {
        System.out.println("---------------------------------------------------- Ethernet Header Info ----------------------------------------");
        this.SeparateEthHeader();
        this.ConvertEthToRedable();
        if(Adjust==true)
        {
            this.AdjustEthFeilds();
        }
        this.PrintArray();
        System.out.println("------------------------------------------------------------------------------------------------------------------");
    }

    void Reset()
    {
        this.SeparateEthHeader();
    }


    void ConvertToTransferable()
    {
        this.Reset();
    }

    void ConvertToNotRedable()
    {
        this.Reset();
    }

    void ResetHeader()
    {
        this.Reset();
    }

    void PrintEthHeader()
    {
        this.PrintArray();
    }
}

class NetworkHeader extends EthernetHeader
{
    //* 2 --> Network Header  (4-4-6-2-16-16-2-14-8-8-16-32-32-32) = 192
    protected String NetDataStream;
    protected final int NetHeaderSize[] = {192,4,4,7,1,16,16,2,14,8,8,16,32,32,32};
    protected final String NetHeaderTokens[] = {"Network Header : ","Version : ","IHL : ","DSCP : ","ECN : ","Total Length : ","Identification : ","Flags : ","Fragment Offset : ","Time To Live : ","Protocol : ","Header Checksum : ","Source IP : ","Destination IP : ","Options : "};
    protected String NetHeaderInfo[];
    private final Pattern dataPattern = Pattern.compile("[^0-1]");
    private Matcher dataMatcherNet;

    NetworkHeader()
    {
        this.NetDataStream = null;
        this.NetHeaderInfo = new String[this.NetHeaderTokens.length];
    }

    NetworkHeader(String DataStream)
    {
        super(DataStream.substring(0,112));
        this.NetDataStream = DataStream;
        this.NetHeaderInfo = new String[this.NetHeaderTokens.length];
        this.dataMatcherNet = this.dataPattern.matcher(this.NetDataStream);
    }

    NetworkHeader(NetworkHeader anotherNetHeader)
    {
        super(anotherNetHeader.NetDataStream.substring(0,112));
        this.NetDataStream = anotherNetHeader.NetDataStream;
        this.NetHeaderInfo = anotherNetHeader.NetHeaderInfo;
        this.dataMatcherNet = anotherNetHeader.dataMatcherNet;
    }

    void ReceiveNet(String data)
    {
        this.NetDataStream = data;
    }

    String SendNet()
    {
        return this.NetDataStream;
    }

    private boolean ValidateHeader(boolean DoReturn)
    {
        this.dataMatcherNet = this.dataPattern.matcher(this.NetDataStream);
        if(this.NetDataStream.length() < this.NetHeaderSize[0])
        {
            if(DoReturn)
            {
                System.out.println("#--> Error Reported By Programmer : Your Network Data Stream Might Have Been Corrupted!");
            }
            return false;
        }
        else if(this.dataMatcherNet.find())
        {
            if(DoReturn)
            {
                System.out.println("#--> Error Reported By Programmer : Your Network Data Stream Contains Some Illegal Bits.");
            }
            return false;
        }
        else if(this.NetDataStream.length() > this.NetHeaderSize[0])
        {
            if(this.NetDataStream.length() < this.EthHeaderSize[0]+this.NetHeaderSize[0])
            {
                if(DoReturn)
                {
                    System.out.println("#--> Error Reported By Programmer : Your Given Stream Is Not Valid. It may be courrupted OR modified by mistaken!");
                }
                return false;
            }
            else
            {
                if(DoReturn)
                {
                    System.out.println("--> Your Given Stream Is Larger Than Required, Rest Data Will Be Sent To Ethernet Layer!");
                }
                this.ReceiveEth(this.NetDataStream.substring(0, this.EthHeaderSize[0]));
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    boolean SeparateNetHeader(boolean DoReturn)
    {
        if(this.ValidateHeader(DoReturn))
        {
            int i=1;
            try
            {
                int LastCounted = 0;
                this.NetHeaderInfo[0] = this.NetDataStream.substring(LastCounted+this.EthHeaderSize[0], this.EthHeaderSize[0]+this.NetHeaderSize[0]);

                for(i=1;i<this.NetHeaderSize.length;i++)
                {
                    //System.out.println(LastCounted);
                    this.NetHeaderInfo[i] = this.NetHeaderInfo[0].substring(LastCounted, LastCounted+this.NetHeaderSize[i]);
                    LastCounted += this.NetHeaderSize[i];
                }
            }
            catch(Exception e)
            {
                System.out.println("#--> Error Reported By JAVA : "+e);
            }

            if(DoReturn)
            {
                System.out.println("--> Network Data Successfully Seprated!");
            }
            return true;
        }
        else
        {
            System.out.println("#--> Error Reported By Programmer : Your Data Stream For Network Header Is Not Valid!");
            if(DoReturn)
            {
                System.out.println("--> Network Data Is Not In State Of Seprable!");
            }
            return false;
        }
    }

    public void SeparateNetHeader()
    {
        this.SeparateNetHeader(false);
    }

    void ConvertNetToRedable()
    {
        if(this.SeparateNetHeader(false))
        {
            try
            {
                int LastCounted = 0;
                String GrabbedValue = new String();
                String FinalValue = new String();
                int i=1;
                int j=1;

                for(i=1;i<this.NetHeaderInfo.length;i++)
                {
                    if(this.NetHeaderInfo[i].length() > 4)
                    {
                        for(j=1;j<=(this.NetHeaderSize[i]/4);j++)
                        {
                            if(this.NetHeaderInfo[i].length()%4==0)
                            {
                                GrabbedValue = this.NetHeaderInfo[i].substring(LastCounted,LastCounted+4);
                                LastCounted+=4;
                            }
                            else
                            {
                                if(j==1)
                                {
                                    GrabbedValue = this.NetHeaderInfo[i].substring(LastCounted,LastCounted+4);
                                    LastCounted += 4;
                                }
                                else
                                {
                                    if(this.NetHeaderInfo[i].length()-LastCounted < 4)
                                    {
                                        GrabbedValue = this.NetHeaderInfo[i].substring(LastCounted,this.NetHeaderInfo[i].length()-1);
                                    }
                                    else
                                    {
                                        GrabbedValue = this.NetHeaderInfo[i].substring(LastCounted,LastCounted+4);
                                        LastCounted += 4;
                                    }
                                }
                                FinalValue = Integer.toHexString(Integer.parseInt(GrabbedValue, 2));
                            }

                            if(j==0)
                            {
                                FinalValue = Integer.toHexString(Integer.parseInt(GrabbedValue,2));
                            }
                            else
                            {
                                FinalValue = FinalValue.concat(Integer.toHexString(Integer.parseInt(GrabbedValue,2)));
                            }
                        }
                        this.NetHeaderInfo[i] = FinalValue;
                        LastCounted = 0;
                        FinalValue = new String();
                    }
                    else
                    {
                        GrabbedValue = this.NetHeaderInfo[i];
                        FinalValue = Integer.toHexString(Integer.parseInt(GrabbedValue, 2));
                        this.NetHeaderInfo[i] = FinalValue;
                    }
                }

            }
            catch(NumberFormatException e)
            {
                System.out.println("#--> Error Reported By JAVA : "+e);
            }
            catch(Exception e)
            {
                System.out.println("#--> Error Reported By JAVA : "+e);
            }
        }
        else
        {
            System.out.println("#--> Error Reported By Programmer : Cannot Perform 'ConvertToRedable' Operation For Network Header!");
        }
    }

    private void PrintArray()
    {
        for(int i=1;i<this.NetHeaderTokens.length;i++)
        {
            System.out.println(this.NetHeaderTokens[i]+" "+this.NetHeaderInfo[i]);
        }
    }

    void RevealNetHeaderInfo()
    {
        this.RevealNetHeaderInfo(false);
    }

    public void RevealNetHeaderInfo(boolean Adjust)
    {
        System.out.println("---------------------------------------------------- Network Header Info ----------------------------------------");
        this.SeparateNetHeader();
        this.ConvertNetToRedable();
        if(Adjust==true)
        {
            //will be adding soon...
            //this.AdjustFeilds();
        }
        this.PrintArray();
        System.out.println("------------------------------------------------------------------------------------------------------------------");
    }

    @Override
    void Reset()
    {
        this.SeparateNetHeader();
    }

    @Override
    void ConvertToTransferable()
    {
        this.Reset();
    }

    @Override
    void ConvertToNotRedable()
    {
        this.Reset();
    }

    @Override
    void ResetHeader()
    {
        this.Reset();
    }

    void PrintNetHeader()
    {
        this.PrintArray();
    }
}

class TcpHeader extends NetworkHeader
{
    //* 3 --> TCP/Transport Header (16-16-32-32-4-6-1-1-1-1-1-1-16) = 128
    protected String TcpDataStream;
    protected final int TcpHeaderSize[] = {128,16,16,32,32,4,6,1,1,1,1,1,1,16};
    protected final String TcpHeaderTokens[] = {"TCP Header : ","Source Port : ","Destination Port : ","Sequence Number : ","Acknowledgement Number : ","Header Length : ","Reserved Bits : ","URG : ","ACK : ","PSH : ","RST : ","SYN : ","FIN : ","Window Size : "};
    protected String TcpHeaderInfo[];
    private final Pattern dataPattern = Pattern.compile("[^0-1]");
    private Matcher dataMatcerTcp;

    TcpHeader()
    {
        this.TcpDataStream = null;
        this.TcpHeaderInfo = new String[this.TcpHeaderTokens.length];
    }

    TcpHeader(String DataStream)
    {
        super(DataStream.substring(0,304));
        this.TcpDataStream = DataStream;
        this.TcpHeaderInfo = new String[this.TcpHeaderTokens.length];
        this.dataMatcerTcp = this.dataPattern.matcher(this.TcpDataStream);
    }

    TcpHeader(TcpHeader anotherNetHeader)
    {
        super(anotherNetHeader.TcpDataStream.substring(0,304));
        this.TcpDataStream = anotherNetHeader.TcpDataStream;
        this.TcpHeaderInfo = anotherNetHeader.TcpHeaderInfo;
        this.dataMatcerTcp = anotherNetHeader.dataMatcerTcp;
    }

    void ReceiveTcp(String data)
    {
        this.TcpDataStream = data;
    }

    String SendTcp()
    {
        return this.TcpDataStream;
    }

    private boolean ValidateHeader(boolean DoReturn)
    {
        this.dataMatcerTcp = this.dataPattern.matcher(this.TcpDataStream);
        if(this.TcpDataStream.length() < this.TcpHeaderSize[0])
        {
            if(DoReturn)
            {
                System.out.println("#--> Error Reported By Programmer : Your TCP Data Stream Might Have Been Corrupted!");
            }
            return false;
        }
        else if(this.dataMatcerTcp.find())
        {
            if(DoReturn)
            {
                System.out.println("#--> Error Reported By Programmer : Your TCP Data Stream Contains Some Illegal Bits.");
            }
            return false;
        }
        else if(this.TcpDataStream.length() > this.TcpHeaderSize[0])
        {
            if(this.TcpDataStream.length() < this.EthHeaderSize[0]+this.NetHeaderSize[0]+this.TcpHeaderSize[0])
            {
                if(DoReturn)
                {
                    System.out.println("#--> Error Reported By Programmer : Your Given Stream Is Not Valid. It may be courrupted OR modified by mistaken!");
                }
                return false;
            }
            else
            {
                if(DoReturn)
                {
                    System.out.println("--> Your Given Stream Is Larger Than Required, Rest Data Will Be Sent To Network Layer!");
                }
                this.ReceiveEth(this.TcpDataStream.substring(0, this.TcpHeaderSize[0]));
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    boolean SeparateTcpHeader(boolean DoReturn)
    {
        if(this.ValidateHeader(DoReturn))
        {
            int i=1;
            try
            {
                int LastCounted = 0;
                this.TcpHeaderInfo[0] = this.TcpDataStream.substring(LastCounted+this.EthHeaderSize[0]+this.NetHeaderSize[0], this.EthHeaderSize[0]+this.NetHeaderSize[0]+this.TcpHeaderSize[0]);

                for(i=1;i<this.TcpHeaderSize.length;i++)
                {
                    //System.out.println(LastCounted);
                    this.TcpHeaderInfo[i] = this.TcpHeaderInfo[0].substring(LastCounted, LastCounted+this.TcpHeaderSize[i]);
                    LastCounted += this.TcpHeaderSize[i];
                }
            }
            catch(Exception e)
            {
                System.out.println("#--> Error Reported By JAVA : "+e);
            }

            if(DoReturn)
            {
                System.out.println("--> TCP Data Successfully Seprated!");
            }
            return true;
        }
        else
        {
            System.out.println("#--> Error Reported By Programmer : Your Data Stream For TCP Header Is Not Valid!");
            if(DoReturn)
            {
                System.out.println("--> TCP Data Is Not In State Of Seprable!");
            }
            return false;
        }
    }

    public void SeparateTcpHeader()
    {
        this.SeparateTcpHeader(false);
    }

    void ConvertTcpToRedable()
    {
        if(this.SeparateTcpHeader(false))
        {
            try
            {
                int LastCounted = 0;
                String GrabbedValue = new String();
                String FinalValue = new String();
                int i=1;
                int j=1;

                for(i=1;i<this.TcpHeaderInfo.length;i++)
                {
                    if(this.TcpHeaderInfo[i].length() > 4)
                    {
                        for(j=1;j<=(this.TcpHeaderSize[i]/4);j++)
                        {
                            if(this.TcpHeaderInfo[i].length()%4==0)
                            {
                                GrabbedValue = this.TcpHeaderInfo[i].substring(LastCounted,LastCounted+4);
                                LastCounted+=4;
                                /*if(this.TcpHeaderInfo.length-1==i)
                                {
                                    System.out.println("GV : "+GrabbedValue);
                                }*/
                            }
                            else
                            {
                                if(j==1)
                                {
                                    GrabbedValue = this.TcpHeaderInfo[i].substring(LastCounted,LastCounted+4);
                                    LastCounted += 4;
                                }
                                else
                                {
                                    if(this.TcpHeaderInfo[i].length()-LastCounted < 4)
                                    {
                                        GrabbedValue = this.TcpHeaderInfo[i].substring(LastCounted,this.TcpHeaderInfo[i].length()-1);
                                    }
                                    else
                                    {
                                        GrabbedValue = this.TcpHeaderInfo[i].substring(LastCounted,LastCounted+4);
                                        LastCounted += 4;
                                    }
                                }
                                FinalValue = Integer.toHexString(Integer.parseInt(GrabbedValue, 2));
                            }

                            if(j==0)
                            {
                                FinalValue = Integer.toHexString(Integer.parseInt(GrabbedValue,2));
                            }
                            else
                            {
                                FinalValue = FinalValue.concat(Integer.toHexString(Integer.parseInt(GrabbedValue,2)));
                            }
                            /*if(this.TcpHeaderSize.length-1==i)
                            {
                                System.out.println("FV : "+FinalValue);
                            }*/
                        }
                        this.TcpHeaderInfo[i] = FinalValue;
                        LastCounted = 0;
                        FinalValue = new String();
                    }
                    else
                    {
                        GrabbedValue = this.TcpHeaderInfo[i];
                        FinalValue = Integer.toHexString(Integer.parseInt(GrabbedValue, 2));
                        this.TcpHeaderInfo[i] = FinalValue;
                    }
                }

            }
            catch(NumberFormatException e)
            {
                System.out.println("#--> Error Reported By JAVA : "+e);
            }
            catch(Exception e)
            {
                System.out.println("#--> Error Reported By JAVA : "+e);
            }
        }
        else
        {
            System.out.println("#--> Error Reported By Programmer : Cannot Perform 'ConvertToRedable' Operation For TCP Header!");
        }
    }

    private void PrintArray()
    {
        for(int i=1;i<this.TcpHeaderTokens.length;i++)
        {
            System.out.println(this.TcpHeaderTokens[i]+" "+this.TcpHeaderInfo[i]);
        }
    }

    void RevealTcpHeaderInfo()
    {
        this.RevealTcpHeaderInfo(false);
    }

    public void RevealTcpHeaderInfo(boolean Adjust)
    {
        System.out.println("---------------------------------------------------- Transport Header Info ----------------------------------------");
        this.SeparateTcpHeader();
        this.ConvertTcpToRedable();
        if(Adjust==true)
        {
            //will be adding soon...
            //this.AdjustFeilds();
        }
        this.PrintArray();
        System.out.println("------------------------------------------------------------------------------------------------------------------");
    }

    @Override
    void Reset()
    {
        this.SeparateTcpHeader();
    }

    @Override
    void ConvertToTransferable()
    {
        this.Reset();
    }

    @Override
    void ConvertToNotRedable()
    {
        this.Reset();
    }

    @Override
    void ResetHeader()
    {
        this.Reset();
    }

    void PrintTcpHeader()
    {
        this.PrintArray();
    }
}

class PhysicalLayer extends TcpHeader
{
    private String DataStream;

    PhysicalLayer()
    {
        this.DataStream = null;
    }

    PhysicalLayer(String DataStream)
    {
        super(DataStream.substring(0,432));
        this.DataStream = DataStream;
    }

    PhysicalLayer(PhysicalLayer anotherLayer)
    {
        super(anotherLayer.DataStream.substring(0,432));
        this.DataStream = anotherLayer.DataStream;
    }

    void RecDataStream(String DataStream)
    {
        this.DataStream = DataStream;
    }

    String SendDataStream()
    {
        return this.DataStream;
    }

    void SeprateHeaders()
    {
        this.SeparateTcpHeader(true);
        this.SeparateNetHeader(true);
        this.SeparateEthHeader();
    }

    void RevealHeaders()
    {
        this.RevealEthHeaderInfo(true);
        this.RevealNetHeaderInfo();
        this.RevealTcpHeaderInfo();
    }

    void ResetAll()
    {
        this.Reset();
    }

    void PrintHeaders()
    {
        this.PrintEthHeader();
        this.PrintNetHeader();
        this.PrintTcpHeader();
    }

}

public class Assignment1_20CS028 {

    static void PrintAboutMe()
    {
        System.out.println("==================================================");
        System.out.println("=     Made By :-YASH KOLADIYA                    =");
        System.out.println("=     Roll No :- 20CS028                         =");
        System.out.println("= If you don't want to work with file then you   =");
        System.out.println("= can also use the class 'PhysicalLayer' Made By =");
        System.out.println("= Me .. And You can also explore all functions   =");
        System.out.println("= Also Individual classes are there for each     =");
        System.out.println("= layer such as Eth.,Network,TCP....Just Explore ");
        System.out.println("==================================================");
    }

    static void AskAboutFile()
    {
        PrintAboutMe();
        File UserFile = null;
        int TotalLines = 0;
        int UserChoice = 1;
        boolean ErrorFound = false;
        ArrayList <Scanner> FileScanners = new ArrayList<>();
        Scanner ConsoleScanner = new Scanner(System.in);
        PhysicalLayer data = null;

        System.out.println("--> Enter The File That You Want To Scan : ");
        String Path = ConsoleScanner.nextLine();

        try
        {
            UserFile = new File(Path);
            FileScanners.add(new Scanner(UserFile));
        }
        catch(FileNotFoundException e)
        {
            ErrorFound = true;
            System.out.println("#--> Error Reported By JAVA : "+e);
            System.out.println("#--> Possible Solution By Programmer : You should enter the valid file path again.");
        }

        if(!ErrorFound)
        {
            String Stream = FileScanners.get(0).nextLine();
            String RefStr = new String();
            data = new PhysicalLayer(Stream);
            if(FileScanners.get(0).hasNextLine())
            {
                while(FileScanners.get(0).hasNextLine())
                {
                    RefStr = FileScanners.get(0).nextLine();
                    TotalLines += 1;
                }
                FileScanners.get(0).close();
                System.out.println("--> There are more "+TotalLines+" Lines Detected In Your File!, Please Spesify For How Many Lines You Want To See Output!");
                System.out.print("--> Enter Liines : ");
                UserChoice = ConsoleScanner.nextInt();

                if(UserChoice <= (TotalLines + 1))
                {
                    FileScanners.clear();

                    try
                    {
                        FileScanners.add(new Scanner(UserFile));
                    }
                    catch(FileNotFoundException e)
                    {
                        System.out.println("#--> Error Reported By JAVA : "+e);
                        System.out.println("#--> Possible Solution By Programmer : Recheck The Existance Of Given File Path!");
                    }

                    //data.PrintHeaders();

                    for(int i=0;i<UserChoice;i++)
                    {
                        System.out.println("\n============================================== OUTPUT FOR LINE "+i+" =================================================");
                        Stream = FileScanners.get(0).nextLine();
                        PhysicalLayer dataNew= new PhysicalLayer(Stream);
                        dataNew.RevealHeaders();
                        System.out.println("===========================================================================================================\n\n");
                    }
                }
                else
                {
                    System.out.println("#--> Error Reported By Programmer : There Are Only "+(TotalLines+1)+" Lines In Your File. Unable To Scan More Than That!");
                    System.out.println("#--> Possible Solution By Programmer : Please, Run Program Again And Try To Input Less Then Total Lines Present.");
                }
            }
            else
            {
                System.out.println("#--> There is only one line detected in your file! So, Here is the Output!");
                data.RevealHeaders();
            }
        }
    }
    public static void main(String[] args) {

        AskAboutFile();
    }
}


