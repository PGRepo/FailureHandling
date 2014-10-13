package com.disaster.recovery.start;


import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
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



public class DisasterRecoveryMain 
{
	public static void main(String[] args) throws Exception
	{
	
	    /*
		 * Fetch values from the property file
		 */
	    
		String _vcenter_url = null;
		String _username = null;
		String _password = null;
		String _log_path = null;
		
		try (FileReader reader = new FileReader("config/config_details.properties")) 
		{
			Properties properties = new Properties();
			properties.load(reader);
			_vcenter_url = properties.getProperty("vCenter_url");
			_username = properties.getProperty("username");
			_password = properties.getProperty("password");
			_log_path = properties.getProperty("log_path");
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
				
		final Logger logger = Logger.getLogger(VMHost.class.getName());
		FileHandler fh = new FileHandler(_log_path);
	    try 
	    {   
	    	
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
	        // To remove logging from console uncomment below startement
	        //logger.setUseParentHandlers(false); 

	    }
	    catch (SecurityException e) 
	    {  
	        e.printStackTrace();  
	    } 
	logger.info("Initialized variables"+logger.getName());
		
		long start = System.currentTimeMillis();
		URL url = new URL(_vcenter_url);
		
		/*
		 * Service Managed object is the singleton root object of the inventory.
		 * This instance can be implemented by a virtual center server  managing a collection of host 
		 * or by a stand alone host agent.
		 * When you create a managed object the server adds them to the inventory.
		 * 	ServiceInstance  -- Root of inventory; created by vsphere
    		Datacenter       -- A container that represents a virtual data center. 
    							It contains hosts, network entities, virtual machines & virtual applications, & datastores.
    		Folder           -- A container used for hierarchical organization of the inventory.
    		VirtualMachine   -- A virtual machine
    		ComputeResource  -- A compute resource (either a cluster or a stand-alone host)
    		ResourcePool     -- A subset of resources provided by a ComputeResource
    		Host             -- A single host (ESX Server or VMware Server) 
    		DataStore		 -- Platform-independent, host-independent storage for virtual machine files.
   
		 */
		
		ServiceInstance serviceinstance = new ServiceInstance(url, _username, _password, true);
		long end = System.currentTimeMillis();
		System.out.println("time taken:" + (end-start));
		Folder rootFolder = serviceinstance.getRootFolder();
		String name = rootFolder.getName();
		System.out.println("root folder name is:" + name);
		
	/*	ManagedEntity[] virtual_machine_me = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		
		if(virtual_machine_me==null || virtual_machine_me.length ==0)
		{
			return;
		}*/
		// get all vcenter
		/*logger.info("Print V center"+logger.getName());
		System.out.println("####################################### V Center ###############################");
		System.out.println("version is "+ serviceinstance.getAboutInfo().version);
		System.out.println(" os type is .. " + serviceinstance.getAboutInfo().osType);
		System.out.println("Vendor is .. " + serviceinstance.getAboutInfo().vendor);
		System.out.println("name is" + serviceinstance.getAboutInfo().name);*/
		
		
		
		//get all host
		VMHost vmhost= new VMHost(serviceinstance, fh);
		List<HostSystem> listOfHost = vmhost.getAllHost();
		
		// get vm under a host
		HostSystem hostname = (HostSystem) new InventoryNavigator(
		        rootFolder).searchManagedEntity(
		            "HostSystem", "130.65.132.193");
		List<VirtualMachine> listOfVMForHost = vmhost.getAvailableVMForHost(hostname);
		
		//create host 
		
		//delete host
		
		// get all vm
		VirtualMachineController vmc = new VirtualMachineController(serviceinstance, fh);
		List<VirtualMachine> listOfVirtualMachine = vmc.getAllVM();
		
		// turn off vm
		VirtualMachine vm_to_turnoff = (VirtualMachine) new InventoryNavigator(
		        rootFolder).searchManagedEntity(
		            "VirtualMachine", "Pratik_VM");
		
		
		vmc.turnoffVM(vm_to_turnoff);
		
		//turn on vm
		VirtualMachine vm_to_turnon = (VirtualMachine) new InventoryNavigator(
		        rootFolder).searchManagedEntity(
		            "VirtualMachine", "Pratik_VM");
		
		//vmc.turnOnVM(vm_to_turnon);
		
		// migrate vm
		
		//vmc.migrateVM("Pratik_VM", "130.65.132.192");
		
		// clone vm
		
		vmc.cloneVM("Pratin_VM_Clone", "Pratik_VM" , "DC-Team07");
		
		// create vm
		
		// remove vm
		
		
	
		
			serviceinstance.getServerConnection().logout();
			
		
		
	}
	
	
	

}
