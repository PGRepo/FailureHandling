package com.myThreads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.controller.host.VMHost;
import com.controller.vm.VirtualMachineController;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;
import com.vmware.vim25.mo.samples.HelloVM;
import com.vmware.vim25.mo.util.OptionSpec;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class PingVm implements Runnable {
	List< VirtualMachine> listOfAvailableVm = new ArrayList<VirtualMachine>();
	Logger logger = Logger.getLogger( VirtualMachineController.class.getName());
	VirtualMachineController vmc;
	ServiceInstance serviceinstance;
	
	/*public PingVm(List<VirtualMachine> vmList)
	{
		listOfAvailableVm = vmList;
		logger.info("got the list of vm to ping"+logger.getName());
	}*/
	
	public PingVm(ServiceInstance si)
	{
		serviceinstance = si;
		 vmc = new VirtualMachineController();
		
	}
	
	
	public Boolean checkMachineStatus(String ip) throws IOException 
	{
		Boolean isReachable = false;
		Runtime r = Runtime.getRuntime();
			Process pingProcess = r.exec("ping " + ip);
			String pingResult = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(
					pingProcess.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) 
			{
				System.out.println(inputLine);
				pingResult += inputLine;
			}
//IN THE CASE WHERE PING FAILS
			if ((pingResult.contains("Request timed out") && !(pingResult.contains("Reply from"))) || ip==null) 
			{
				System.out.println("Host Not Found");
				isReachable = false;
			} 
// IN CASE OF PING SUCCESS 			
			else 
			{
				isReachable = true;
				System.out.println("Host is live");
			}
		return isReachable;
	}
	
	
	
	
	/*private boolean ping(String ipAddress)
	{
		try {
			InetAddress address = InetAddress.getByName(ipAddress);
			
				boolean reachable = address.isReachable(1000);
				if(reachable)
				{
					return true;
				}
				
					
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return false;
		
	}*/
	
	
	public void startPing()
	{
		listOfAvailableVm = vmc.getAllVM(serviceinstance);
		for(VirtualMachine vm : listOfAvailableVm)
		{
			
			String ipAddress = vm.getSummary().getGuest().getIpAddress();
    		System.out.println("Triggered alarm...."+vm.getTriggeredAlarmState());
			System.out.println("ip address"+ ipAddress+ "name is "+vm.getName());
			if(ipAddress == null)
			{
				System.out.println("ip address is null so skiping ping to this machine..."+ vm.getName());
			}
			try
	        {
				logger.info("pinging..."+vm.getName()+"  at ip: "+ipAddress+"..."+logger.getName());
				
	            if(!checkMachineStatus(ipAddress))
	            {
	            	// try to reconnect
	            	
	            	boolean reached =false;
	            	logger.info("vm is not reachable....trying to reconnect...Please wait");
	            	for (int i=0;i< 1; i++)
	            	{
	            		reached = checkMachineStatus(ipAddress);//address.isReachable(1000);
	            		if(reached)
	            		{
	            			logger.info("vm is reachable...");
	            			
	            			break;
	            		}
	            		
	            		logger.info("vm is not reachable....trying to reconnect....Please wait");
	            		
	            	
	            	}
	            	if(!reached)
	            	{
	            		
	            		// check if host is down...
	            		
	            		String host_ip = vm.getResourcePool().getParent().getName();
	            		logger.info("vm is not responding it may be down or powered off or host may be down....");
	            		logger.info("Trying to reach out to host..."+ host_ip);
						boolean host_reachable = checkMachineStatus(host_ip);
						VMHost vh =new VMHost();
						Boolean host_connected = false;
        				List<HostSystem> listOfHost = vh.getAllHost(serviceinstance);
        				for(HostSystem hs: listOfHost)
        				{
        					if(hs.getName().equalsIgnoreCase(host_ip))
        							{
        								VMHost hostcontroller = new VMHost();
        								if(hostcontroller.checkHostConnectivity(hs))
        								{
        									host_connected = true;
        								}
        								
        							}
        				}
	            		if(host_reachable && host_connected)
	            		{
	            			
	            				
	            				System.out.println("Host seems to be alright");
	            				System.out.println("Checking power state of the machine...");
	    	            		VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
	    	            		if(vmri.getPowerState() == VirtualMachinePowerState.poweredOn)
	    	            		{		
	    	            			System.out.println("The system has broken down...");
	    	            			System.out.println("Applying snap shot...");
	    	            			
	    	            			if(vmc.is_SnapShot_Available(vm))
		            				{
		            					System.out.println("Reverting vm back to previous state...");
		            				
		    	            			if(vmc.revertToSnapShot(vm))
		    	            			{
		    	            				System.out.println("sucessfully Reverted to last snapshot state");
		    	            			}
		            				}
	    		            		
	    	            		}
	    	            		else
	    	            		{
	    	            			System.out.println("Saale machine chalu kar ping hoga....");
	    	            			System.out.println("Checking next vm");
	    	            		}
	            				
	            				
	            			

	            		}
	            		else
	            		{
	            				
	            				System.out.println("Host is down");
	            				System.out.println("Applying snapshot");
	            				
	            				System.out.println("Trying to recover host...");
	            				VMHost hostController = new VMHost();
	            				if(!hostController.recoverHost())
	            				{
		            				if(vmc.is_SnapShot_Available(vm))
		            				{
		            					System.out.println("Reverting vm back to previous state...");
		            				
		    	            			if(vmc.revertToSnapShot(vm))
		    	            			{
		    	            				System.out.println("sucessfully Reverted to last snapshot state");
		    	            			}
		            				}
		            				else{
		            					System.out.println("The host do not have any snapshot to recover. Sorry !!!!");
		            				}
	            				}
	            				else
	            				{
	            					System.out.println("Host sucessfully Recovered");
	            					
	            				}
	            				
	            				
	            			
	            		}
	            		
	            		
	            		
	            		// check the power status of vm
	            		
	            		

	            		
	            	}
	            	
	            }
	            else
	            {
	            	
	            	logger.info("Got response from vm...");
	            }
	            

	            
	        }
			
			catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	
		
	
	
	}
		
	}

	@Override
	public void run() {
		
	while(true)
		{
		startPing();
		try {
			System.out.println("Wait for 30 seconds....");
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		
	}

}
