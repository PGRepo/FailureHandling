


import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

import com.controller.host.VMHost;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;
import com.vmware.vim25.mo.samples.HelloVM;
import com.vmware.vim25.mo.util.OptionSpec;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;



public class lab3 
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
		ManagedEntity[] hostmanagedEntities;
		
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
	    try 
	    {   
	    	FileHandler fh = new FileHandler(_log_path);
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
		
		logger.info("Print V center"+logger.getName());
		System.out.println("####################################### V Center ###############################");
		System.out.println("version is "+ serviceinstance.getAboutInfo().version);
		System.out.println(" os type is .. " + serviceinstance.getAboutInfo().osType);
		System.out.println("Vendor is .. " + serviceinstance.getAboutInfo().vendor);
		System.out.println("name is" + serviceinstance.getAboutInfo().name);
		
		
		
		
		try {
			hostmanagedEntities = new InventoryNavigator(
					serviceinstance.getRootFolder()).searchManagedEntities("HostSystem");
			HostSystem host = null;
			logger.info("List of available host in vcenter"+logger.getName());
			for (ManagedEntity managedEntity : hostmanagedEntities) 
			{
				System.out.println();
				host = (HostSystem)managedEntity;
				System.out.println();
				if(host !=null)
					{
						System.out.println("Host name: '" + host.getName() + "'");
						System.out.println("is alarm enabled?"+host.getAlarmActionEabled());
						HostHardwareInfo hw = host.getHardware();
						String ESXhostmodel = hw.getSystemInfo().getModel();
						String Vendor = hw.getSystemInfo().getVendor();
						System.out.println("ESX host model is: "+ESXhostmodel);
						System.out.println("Vendor is: "+ Vendor);
						System.out.println("data store for host"+host.getDatastores());
						System.out.println("Host has below virtual machines.......");
						System.out.println("Host has below virtual machines.......");
						int i=1;
						logger.info("List of vm under host"+logger.getName());
						for(VirtualMachine v : host.getVms())
							{
									System.out.println("Details for Virtual Machine"+ i + "are...");
									System.out.println();
									System.out.println("Name of virtual machine : "+v.getName());
									System.out.println("vm wayer version is ..from inventory.. "+ v.getConfig().version);
									System.out.println("geust os uuid "+v.getSummary().getConfig().uuid);
									//System.out.println("geust mac Address of guest  "+macAddress);
									System.out.println("geust id is "+v.getSummary().getGuest().guestId);
									System.out.println("Virtual machine ip address is "+v.getSummary().getGuest().getIpAddress());
									System.out.println("virtual machine status is"+ v.getSummary().runtime.powerState);
									System.out.println("Data store of vm is"+ v.getDatastores());
									System.out.println("GuestOS: " + v.getConfig().getGuestFullName());
									System.out.println("Multiple snapshot supported: " + v.getCapability().isMultipleSnapshotsSupported());
									System.out.println("path of vm is"+ v.getSummary().config.getVmPathName());
									logger.info("checking for the power status...");
									
									if(v.getSummary().runtime.powerState.toString().equals("poweredOn"))
										
									{
											logger.info("The virtual machine is on. Truneing it off");
										    Task t=v.powerOffVM_Task();
										    if(t.waitForMe()==Task.SUCCESS)
										    {
										    System.out.println("Machine is turned off now!");
										    }
										    else
										    {
										    System.out.println("failed to turn off machine!");
										    }
				
										    
									}
									else
										{
											logger.info("Machine is already powerd off!!");
										    Task t = v.powerOnVM_Task(null);
											 if(t.waitForMe()==Task.SUCCESS)
											    {
												 	System.out.println("Machine is turned on now!");
											    }
											 else
											    {
												 	System.out.println("failed to turn on machine!");
											    }
										}
										
										i++;
						}
					
						
						
						
						
					}
			}
						
		
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		
boolean a = false;
	    if(a)
	    {
		
		String vmname = "Pratik_VM";
	    String newHostName = "130.65.132.193";
	
	System.out.println("#############################-----Migrate---#################################################");
	logger.info("Migrate process started"+logger.getName());
    VirtualMachine vm = (VirtualMachine) new InventoryNavigator(
        rootFolder).searchManagedEntity(
            "VirtualMachine", vmname);
    HostSystem newHost = (HostSystem) new InventoryNavigator(
        rootFolder).searchManagedEntity(
            "HostSystem", newHostName);
    ComputeResource cr = (ComputeResource) newHost.getParent();
    
    String[] checks = new String[] {"cpu", "software"};
    HostVMotionCompatibility[] vmcs =
      serviceinstance.queryVMotionCompatibility(vm, new HostSystem[] 
         {newHost},checks );
    
    String[] comps = vmcs[0].getCompatibility();
    if(checks.length != comps.length)
    {
      System.out.println("CPU/software NOT compatible. Exit.");
      serviceinstance.getServerConnection().logout();
      return;
    }
    
    Task task = vm.migrateVM_Task(cr.getResourcePool(), newHost,
        VirtualMachineMovePriority.defaultPriority, 
        VirtualMachinePowerState.poweredOff);
  
    if(task.waitForMe()==Task.SUCCESS)
    {
      System.out.println("VMotioned!");
    }
    else
    {
      System.out.println("VMotion failed!");
      TaskInfo info = task.getTaskInfo();
      System.out.println(info.getError().getFault());
    }
		
	}
	    

		
    System.out.println("############################ Clone Virtual Machine Begines #################");
    
    
	   String cloneName = "Pratik_VM_Cloned";
	   String vm_to_clone_name = "Pratik_VM";
	   //String vmPath = "";
	   VirtualMachine vm_to_clone = null;
	   String datacenterName= "DC-Team07"; // 
	   
	   
	   // get vm to be cloned
	  
	   ManagedEntity[] virtual_machine_me = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		
		if(virtual_machine_me==null || virtual_machine_me.length ==0)
		{
			logger.info("no virtual machine found");
			return;
		}
		else
		{
			for(ManagedEntity me: virtual_machine_me)
			{
				VirtualMachine vm = (VirtualMachine) me;
				if(vm.getName().equalsIgnoreCase(vm_to_clone_name))
				{
					vm_to_clone = vm;
					logger.info("vm to clone is found");
				}
				else{
					logger.info("Vm to be cloned is not found");
				}
				
				
			}
		}
	   
	   try
	   {
		   
		   // get datacenter
		   
		   Datacenter dc = (Datacenter) serviceinstance.getSearchIndex().findByInventoryPath(datacenterName);
		   
		  // VirtualMachine vm = (VirtualMachine) serviceinstance.getSearchIndex().findByInventoryPath(vm_to_clone_name);
		   if(dc ==null)
		   {
			   System.out.println("VirtualMachine or Datacenter path is NOT correct. Pls double check. ");
			   return;
		   }
		   Folder vmFolder = dc.getVmFolder();
	
		   VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
		   cloneSpec.setLocation(new VirtualMachineRelocateSpec());
		   cloneSpec.setPowerOn(false);
		   cloneSpec.setTemplate(false);

		   Task task = vm_to_clone.cloneVM_Task(vmFolder, cloneName, cloneSpec);
		   System.out.println("Launching the VM clone task. It might take a while. Please wait for the result ...");
		   
		   String status = 	task.waitForMe();
		   if(status==Task.SUCCESS)
		   {
	            System.out.println("Virtual Machine got cloned successfully.");
		   }
		   else
		   {
			   System.out.println("Failure -: Virtual Machine cannot be cloned");
		   }
	   }
	   catch(RemoteException re)
	   {
		   re.printStackTrace();
	   }
	  /* catch(MalformedURLException mue)
	   {
		   mue.printStackTrace();
	   }*/
	
		
			serviceinstance.getServerConnection().logout();
			
		
		
	}
	
	
	

}
