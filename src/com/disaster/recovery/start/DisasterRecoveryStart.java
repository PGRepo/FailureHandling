package com.disaster.recovery.start;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.controller.host.VMHost;
import com.controller.vm.VirtualMachineController;
import com.myThreads.PingVm;
import com.myThreads.snapShotVM;
import com.vmware.vim25.AlarmSetting;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.AlarmTriggeringAction;
import com.vmware.vim25.DuplicateName;
import com.vmware.vim25.InvalidName;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SendEmailAction;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.mo.*;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DisasterRecoveryStart 
{
	
    private static  ServiceInstance serviceinstance_vm;
    static Alarm[] alarmList;
    private static  ServiceInstance serviceinstance_host;
    
    public static void createAlarm(VirtualMachine vm,
			ServiceInstance si) 
			{
		boolean alarmExists=false;
		System.out.println("------------------------------------------");
		 System.out.println("Creating Alarm: ");
		AlarmManager am = si.getAlarmManager();
		try {
			alarmList =am.getAlarm(vm);
		} catch (RuntimeFault e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (Alarm a : alarmList) {
			if(a.getAlarmInfo().name.contains("VmPowerStateAlarm"))
				{
					alarmExists=true;
					System.out.println("Alarm already set...");
					break;
				}
		}
		if(!alarmExists)
		{
				StateAlarmExpression sae = new StateAlarmExpression();
				sae.setType("VirtualMachine");
				sae.setStatePath("runtime.powerState");
				sae.setOperator(StateAlarmOperator.isEqual);
				sae.setRed("poweredOff");
				
				SendEmailAction action = new SendEmailAction();
				action.setToList("pratik.p.gaglani@gmail.com");
				action.setCcList("pratik.p.gaglani@gmail.com");
				action.setSubject("Alarm trigger");
				action.setBody("User powered off the VM.");
				
				AlarmTriggeringAction alarmAction = new AlarmTriggeringAction();
				alarmAction.setYellow2red(true);
				alarmAction.setAction(action);
				AlarmSetting as = new AlarmSetting();
				as.setReportingFrequency(0); // as often as possible
				as.setToleranceRange(0);
				AlarmSpec spec = new AlarmSpec();
				spec.setAction(alarmAction);
				spec.setExpression(sae);
				Random r=new Random();
				//int alarmNum=r.nextInt();
				//System.out.println(alarmNum);
				spec.setName("VmPowerStateAlarm");
				spec.setDescription("Monitor VM state and send email if VM power's off");
				spec.setEnabled(true);
				spec.setSetting(as);
				try {
					am.createAlarm(vm, spec);
				} catch (InvalidName e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DuplicateName e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RuntimeFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Successfully created Alarm: AlarmOnPowerOff for "+vm.getName());
				}
	}
    
    
        
	public static void main(String[] args) throws Exception
	{
	    /*
		 * Fetch values from the property file
		 */
	    
		String _vcenter_url_for_vm = null;
		String _vcenter_url_for_host = null;
		String _username = null;
		String _password = null;
		String _log_path = null;
		try (FileReader reader = new FileReader("config/config_details.properties")) 
		{
			Properties properties = new Properties();
			properties.load(reader);
			_vcenter_url_for_vm = properties.getProperty("vCenter_url");
			_vcenter_url_for_host = properties.getProperty("vCenter_url_for_host");
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
		URL url_vcenter_for_host = new URL(_vcenter_url_for_host);
		
		
		serviceinstance_vm = new ServiceInstance(url_vcenter_for_vm, _username, _password, true);
		serviceinstance_host = new ServiceInstance(url_vcenter_for_host, _username, _password, true);
		long end = System.currentTimeMillis();
		System.out.println("time taken:" + (end-start));
		Folder rootFolder_vm = serviceinstance_vm.getRootFolder();
		Folder rootFolder_host = serviceinstance_host.getRootFolder();
		
		
		//System.out.println("root folder name to get access to vm is:" + rootFolder_vm.getName());
		System.out.println("root folder name to get access to host is:" + rootFolder_vm.getName());
		
		
		
		
		ManagedEntity[] virtual_machine_me = new InventoryNavigator(rootFolder_vm).searchManagedEntities("VirtualMachine");
		
		if(virtual_machine_me==null || virtual_machine_me.length ==0)
		{
			return;
		}
		// get all vcenter
		logger.info("Print V center"+logger.getName());
		System.out.println("####################################### V Center ###############################");
		System.out.println("version is "+ serviceinstance_vm.getAboutInfo().version);
		System.out.println(" os type is .. " + serviceinstance_vm.getAboutInfo().osType);
		System.out.println("Vendor is .. " + serviceinstance_vm.getAboutInfo().vendor);
		System.out.println("name is" + serviceinstance_vm.getAboutInfo().name);
				
		/*
		 * Get all vm and set up alarm
		 */
		
		VirtualMachineController vmc = new VirtualMachineController();
		List<VirtualMachine> listOfVirtualMachine = vmc.getAllVM(serviceinstance_vm);
		for(VirtualMachine vm : listOfVirtualMachine)
		{
		createAlarm(vm, serviceinstance_vm);
		
		}
		
		
		Thread pingThread = new Thread(new PingVm(serviceinstance_vm));
		Thread snapShotThread = new Thread(new snapShotVM(serviceinstance_vm,serviceinstance_host));
		//pingThread.start();
		snapShotThread.start();
		
		
		// get all host names
		/*VMHost vmh = new VMHost();
		List<HostSystem> host_list=vmh.getAllHost(serviceinstance_vm);
		for(HostSystem hs: host_list)
		{
			System.out.println("*****************Host Name***************");
			System.out.println(hs.getName());
			vmh.checkHostConnectivity(hs);
			
			
			
		}
		*/
		List<VirtualMachine> a =new ArrayList<VirtualMachine>();
		
		// get all host names from vcenter admin
		
		//List<VirtualMachine> listofHostFromAdmin=vmc.getAllVM(serviceinstance_host);
		/*List <HostSystem> listofHostFromAdmin = vmh.getAllHost(serviceinstance_host);
		for(HostSystem hs: listofHostFromAdmin)
		{
			System.out.println(hs.getName());
					
			
		}*/
		/*for(VirtualMachine vm: listofHostFromAdmin)
		{
			//System.out.println("################################Host Name Admin######################");
			
			//System.out.println(vm.getConfig().name);
			//System.out.println(vm.getResourcePool());
			//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$Resource Pool$$$$$$$$$$$$$$$");
			
			if(vm!= null && vm.getResourcePool() != null)
			{
			 
			//System.out.println(vm.getResourcePool().getName());
			try{
			if(vm.getResourcePool().getName().equalsIgnoreCase("Team07_vHosts"))
			{
				System.out.println(vm.getResourcePool().getName());
				System.out.println(vm.getName());
				// start snapshot process for this host
				
			}
			}
			catch(NullPointerException e)
			{
				System.out.println("catched exception");
				continue;
			}
			}
			
			
		}*/
		
		
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

