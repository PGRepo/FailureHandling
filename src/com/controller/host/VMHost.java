package com.controller.host;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.HostNotConnected;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.samples.HelloVM;

public class VMHost
{
	//ServiceInstance _service_instance;
	Logger logger = Logger.getLogger(VMHost.class.getName());;
	
	/*
	 * Constructor to initialize the parameters for servie instance and file handler
	 */
	public VMHost()
	{
		//_service_instance = si;
		
	}
	
	
	/*
	 * Method to get list of available host
	 */
	
	public List<HostSystem> getAllHost(ServiceInstance _service_instance)
	{
		ManagedEntity[] hostmanagedEntities;
		List< HostSystem> listOfHost = new ArrayList<HostSystem>();
		
		try {
			hostmanagedEntities = new InventoryNavigator(
					_service_instance.getRootFolder()).searchManagedEntities("HostSystem");
			HostSystem host = null;
			logger.info("get list of all host....  "+logger.getName());
			
			for (ManagedEntity managedEntity : hostmanagedEntities) 
			{
				System.out.println();
				host = (HostSystem)managedEntity;
				if(host !=null)
					{
						listOfHost.add(host);
						System.out.println("Host name: '" + host.getName() + "'");

						//System.out.println("is alarm enabled?"+host.getAlarmActionEabled());
						//HostHardwareInfo hw = host.getHardware();
						//String ESXhostmodel = hw.getSystemInfo().getModel();
						//String Vendor = hw.getSystemInfo().getVendor();
						//System.out.println("ESX host model is: "+ESXhostmodel);
						//System.out.println("Vendor is: "+ Vendor);
						//System.out.println("data store for host"+host.getDatastores());
						//System.out.println("Host has below virtual machines.......");
					}
			}
			
						
		
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listOfHost;
		
	}
	
	/*
	 * Method to create host
	 */
	
	public boolean checkHostConnectivity(HostSystem hs)
	{
		System.out.println("Checking the state of connectivity for host"+ hs.getName());
		//HostConnectSpec hcs = hs.;
		//hcs.setUserName("administrator");
		//hcs.setPassword("12!@qwQW");
	
			//HostNotConnected hnc = new HostNotConnected();
			
			//System.out.println("getting session for healthstatussystem"+hs.getHealthStatusSystem().getServerConnection().getSessionStr());
			//System.out.println("Getting connection"+hs.getHealthStatusSystem().getServerConnection());
			if(hs.getSummary().runtime.connectionState.toString().equalsIgnoreCase("connected"))
			{
				return true;
			}
			//System.out.println("Power state of machine is"+hs.getRuntime().getPowerState());
			//System.out.println("config detils are"+hs.getSystemResources().config);
			//System.out.println("trying to disconnect host...");
			//Task t;
			/*if(hs.getName().equalsIgnoreCase("130.65.132.192"))
					{
				
						t=hs.disconnectHost();
					    if(t.waitForMe()==Task.SUCCESS)
					    {
					    	System.out.println("Disconnected host");
					    }
					}*/
			//System.out.println("trying to reconnect host...");
			//String conn = hs.getSummary().runtime.connectionState.toString();
			/*if( hs.getName().equalsIgnoreCase("130.65.132.193"))
			{
				//t = hs.reconnectHost_Task(hcs);
				if(t.waitForMe()==Task.SUCCESS)
			    {
			    	System.out.println("Reconnected host");
			    }
			}*/
			
			//
			
		return false;
	}
	
	/*
	 * Method to delete host
	 */
	
	public boolean recoverHost()
	{
		return false;
		
	}
	
	/*
	 * Method to get avaliable vms for a host
	 */
	
	public List<VirtualMachine> getAvailableVMForHost(HostSystem host)
	{	
	
		List<VirtualMachine> listOfVMForHost = new ArrayList<VirtualMachine>();
		try {
			
				System.out.println();
				if(host !=null)
					{
						//System.out.println("Host name: '" + host.getName() + "'");
						//System.out.println("Host has below virtual machines.......");
				
						int i=1;
						logger.info("Fetch list of vm under a host....."+logger.getName());
						for(VirtualMachine v : host.getVms())
						{
								System.out.println("Details for Virtual Machine"+ i + "are...");
								System.out.println();
							    System.out.println("Name of virtual machine : "+v.getName());
							    String ipAddress = v.getSummary().getGuest().getIpAddress();
								System.out.println("Virtual machine ip address is "+ ipAddress);
							
								
								//System.out.println("vm wayer version is ..from inventory.. "+ v.getConfig().version);
								//System.out.println("geust os uuid "+v.getSummary().getConfig().uuid);
								//System.out.println("geust mac Address of guest  "+macAddress);
								//System.out.println("geust id is "+v.getSummary().getGuest().guestId);
								//System.out.println("Virtual machine ip address is "+v.getSummary().getGuest().getIpAddress());
								//System.out.println("virtual machine status is"+ v.getSummary().runtime.powerState);
								//System.out.println("Data store of vm is"+ v.getDatastores());
								//System.out.println("GuestOS: " + v.getConfig().getGuestFullName());
								//System.out.println("Multiple snapshot supported: " + v.getCapability().isMultipleSnapshotsSupported());
								//System.out.println("path of vm is"+ v.getSummary().config.getVmPathName());
								i++;
								listOfVMForHost.add(v);
				}
						
			}
		
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listOfVMForHost;
	}
	
	
			
			
			

}
