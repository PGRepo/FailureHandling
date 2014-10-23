package com.disaster.recovery.start;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.myThread.Controller.myThreadController;
import com.myThreads.PingVm;
import com.myThreads.snapShotVM;



public class DisasterRecoveryMain  
{
	private static ExecutorService executor = null;
    private static volatile Future taskOneResults = null;
    private static volatile Future taskTwoResults = null;
    private static  ServiceInstance serviceinstance_vm;
    private static  ServiceInstance serviceinstance_host;
    
    /*private static void PingTasks() throws Exception {
        if (taskOneResults == null
                || taskOneResults.isDone()
                || taskOneResults.isCancelled())
        {
        	VirtualMachineController vmc = new VirtualMachineController(serviceinstance);
    		List<VirtualMachine> listOfVirtualMachine = vmc.getAllVM();
            taskOneResults = executor.submit(new PingVm(listOfVirtualMachine));
        }
 
       
    }
    
    private static void snapShotTask()
    {
    	if (taskTwoResults == null
                || taskTwoResults.isDone()
                || taskTwoResults.isCancelled())
        {
            taskTwoResults = executor.submit(new snapShotVM());
        }
    }*/
	
	public static void main(String[] args) throws Exception
	{
		
	
	    /*
		 * Fetch values from the property file
		 */
	    
		String _vcenter_url_for_vm = null;
		//String _vcenter_url_for_host = null;
		String _username = null;
		String _password = null;
		String _log_path = null;
		System.out.println("haha");
		try (FileReader reader = new FileReader("config/config_details.properties")) 
		{
			Properties properties = new Properties();
			properties.load(reader);
			_vcenter_url_for_vm = properties.getProperty("vCenter_url_for_vm");
			//_vcenter_url_for_host = properties.getProperty("vCenter_url_for_host");
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
		URL url_vcenter_for_vm = new URL(_vcenter_url_for_vm);
		//URL url_vcenter_for_host = new URL(_vcenter_url_for_host);
		
		
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
		
		serviceinstance_vm = new ServiceInstance(url_vcenter_for_vm, _username, _password, true);
		//serviceinstance_host = new ServiceInstance(url_vcenter_for_host, _username, _password, true);
		long end = System.currentTimeMillis();
		System.out.println("time taken:" + (end-start));
		Folder rootFolder_vm = serviceinstance_vm.getRootFolder();
		//Folder rootFolder_host = serviceinstance_host.getRootFolder();
		
		
		//System.out.println("root folder name to get access to vm is:" + rootFolder_vm.getName());
		System.out.println("root folder name to get access to host is:" + rootFolder_vm.getName());
		
		//Thread.sleep(10000);
		
		/*BlockingQueue<Runnable> worksQueue = new ArrayBlockingQueue<Runnable>(2);
        RejectedExecutionHandler rejectionHandler = new RejectedExecutionHandelerImpl();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 10, TimeUnit.SECONDS, worksQueue, rejectionHandler);
 
        executor.prestartAllCoreThreads();
         
        List<Runnable> taskGroup = new ArrayList<Runnable>();
        taskGroup.add(new PingVm(serviceinstance));
        //taskGroup.add(new snapShotVM());
        //taskGroup.add(new TestThree());
        worksQueue.put(new PingVm(serviceinstance));
  
        worksQueue.add(new myThreadController(taskGroup));
        worksQueue.remove();*/
		
		
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
		
		//VirtualMachineController vmc = new VirtualMachineController(serviceinstance_vm);
		//List<VirtualMachine> listOfVirtualMachine = vmc.getAllVM();
		//Thread pingThread = new Thread(new PingVm(serviceinstance_vm));
		//Thread snapShotThread = new Thread(new snapShotVM(serviceinstance_vm));
		
			
		//pingThread.start();
		//snapShotThread.start();
		
		
		
		InetAddress address = InetAddress.getByName("130.65.132.182");
		if(address.isReachable(100000))
			{
			System.out.println("reached");
			}
		else
		{
			System.out.println("not reachable");
		}
		
	VirtualMachineController vmc = new VirtualMachineController();
		vmc.ping("130.65.132.192");
		
		 
	/*	executor = Executors.newFixedThreadPool(2);
       while (true)
        {
            try
            {
                PingTasks();
                snapShotTask();
                Thread.sleep(100000);
            } catch (Exception e) {
                System.err.println("Caught exception: " + e.getMessage());
            }
        }
    
		*/
		
		/*
		
		
		//get all host
		VMHost vmhost= new VMHost(serviceinstance, fh);
		//List<HostSystem> listOfHost = vmhost.getAllHost();
		
		// get vm under a host
		HostSystem hostname = (HostSystem) new InventoryNavigator(
		        rootFolder).searchManagedEntity(
		            "HostSystem", "130.65.132.192");
		//List<VirtualMachine> listOfVMForHost = vmhost.getAvailableVMForHost(hostname);
		
		//create host 
		
		//delete host
		
		// get all vm
		VirtualMachineController vmc = new VirtualMachineController(serviceinstance);
		List<VirtualMachine> listOfVirtualMachine = vmc.getAllVM();
		//PingVm pingMachine = new PingVm(listOfVirtualMachine);
		for(VirtualMachine vm : listOfVirtualMachine)
		{
			
			String ipAddress = vm.getSummary().getGuest().getIpAddress();
			System.out.println("ip address"+ ipAddress+ "name is "+vm.getName());
			
		}
		
		Thread ping = new Thread(new PingVm(listOfVirtualMachine));
		ping.start();
		
		
		
		//ping vm
		
		
		
		// turn off vm
		VirtualMachine vm_to_turnoff = (VirtualMachine) new InventoryNavigator(
		        rootFolder).searchManagedEntity(
		            "VirtualMachine", "Pratik_VM");
		
		
		//vmc.turnoffVM(vm_to_turnoff);
		
		//turn on vm
		VirtualMachine vm_to_turnon = (VirtualMachine) new InventoryNavigator(
		        rootFolder).searchManagedEntity(
		            "VirtualMachine", "Pratik_VM");
		
		// ping vm
		
		String ip = "130.65.133.189";
		
		
        String pingResult = "";

        String pingCmd = "ping " + ip;
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);
            
            

            BufferedReader in = new BufferedReader(new
            InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                pingResult += inputLine;
               
            }
            int status = p.waitFor();
            if(status == 0)
            	System.out.println("alive");
            in.close();

        } catch (IOException e) {
            System.out.println(e);
        }
		
		
		//vmc.pingVM(ip);
		//
        
        // Create Snapshot of vm
        
        VirtualMachine vm = (VirtualMachine) new InventoryNavigator(
		        rootFolder).searchManagedEntity(
		            "VirtualMachine", "Pratik_VM");
       // vmc.createSnapShot(vm);

		//vmc.turnOnVM(vm_to_turnon);
		
		// migrate vm
		
		//vmc.migrateVM("Pratik_VM", "130.65.132.192");
		
		// clone vm
		
		//vmc.cloneVM("Pratik_VM_Clone", "Pratik_VM" , "DC-Team07");
		
		// create vm
		
		// remove vm
		
		
	
		
			serviceinstance.getServerConnection().logout();
*/			
		
		
	}
	
	
	

}

class RejectedExecutionHandelerImpl implements RejectedExecutionHandler
{
    @Override
    public void rejectedExecution(Runnable runnable,
            ThreadPoolExecutor executor)
    {
        System.out.println(runnable.toString() + " : I've been rejected ! ");
    }
}
