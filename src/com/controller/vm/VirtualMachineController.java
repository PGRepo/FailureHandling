package com.controller.vm;



import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
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


public class VirtualMachineController
{
	ServiceInstance serviceinstance;
	Logger logger = Logger.getLogger( VirtualMachineController.class.getName());
	
	public VirtualMachineController(ServiceInstance si, FileHandler fh)
	{
		serviceinstance = si;
			
	}
	
	public List<VirtualMachine> getAllVM()
	{
		Folder rootFolder = serviceinstance.getRootFolder();
		ManagedEntity[] virtual_machine_me;
		List<VirtualMachine> listOfVM = new ArrayList<VirtualMachine>();
		try 
		{
			
			virtual_machine_me = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		
			if(virtual_machine_me==null || virtual_machine_me.length ==0)
			{
				logger.info("No vm found"+logger.getName());
				return listOfVM;
			}
			else
			{
				for(ManagedEntity me: virtual_machine_me)
				{
					VirtualMachine vm = (VirtualMachine) me;
					listOfVM.add(vm);
					System.out.println(vm.getName());
					
				}
			}
		
		}
		catch ( RemoteException e) 
		{
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return listOfVM;
			
			
			
	}
	
	public boolean turnoffVM(VirtualMachine vm)
	{
		if(vm.getSummary().runtime.powerState.toString().equals("poweredOn"))
			
		{
				logger.info("The virtual machine is on. Truneing it off");
			    Task t;
				try {
					t = vm.powerOffVM_Task();
				
				    if(t.waitForMe()==Task.SUCCESS)
				    {
				    	System.out.println("Machine is turned off now!");
				    	return true;
				    }
				    else
				    {
				    	System.out.println("failed to turn off machine!");
				    	return false;
				    }
				}
				 catch (TaskInProgress e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidState e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RuntimeFault e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				return false;

			    
		}
		else
			{
				logger.info("Machine is already powerd off!!");
				return true;
			    
			}
		
	}
	
	public boolean turnOnVM(VirtualMachine vm)
	{
		if(vm.getSummary().runtime.powerState.toString().equals("poweredOff"))
			
		{
				logger.info("The virtual machine is off. Truneing it on");
			    Task t;
				try {
					t = vm.powerOnVM_Task(null);
				
				    if(t.waitForMe()==Task.SUCCESS)
				    {
				    	System.out.println("Machine is turned on now!");
				    	return true;
				    }
				    else
				    {
				    	System.out.println("failed to turn off machine!");
				    	return false;
				    }
				}
				 catch (TaskInProgress e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidState e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RuntimeFault e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				return false;

			    
		}
		else
			{
				logger.info("Machine is already powerd on!!");
				return true;
			    
			}
		
	}
	
	public boolean migrateVM(String vmname,String newHostName)
	{
		//String vmname = "Pratik_VM";
	    //String newHostName = "130.65.132.193";
	    Folder rootFolder = serviceinstance.getRootFolder();

	logger.info("Migrate process started"+logger.getName());
    VirtualMachine vm;
	try {
		vm = (VirtualMachine) new InventoryNavigator(
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
      //System.out.println("CPU/software NOT compatible. Exit.");
      logger.info("CPU/software NOT compatible. Exit."+logger.getName());
      serviceinstance.getServerConnection().logout();
      return false;
    }
    
    Task task = vm.migrateVM_Task(cr.getResourcePool(), newHost,
        VirtualMachineMovePriority.defaultPriority, 
        VirtualMachinePowerState.poweredOff);
  
    if(task.waitForMe()==Task.SUCCESS)
    {
      System.out.println("VMotioned!");
      return true;
    }
    else
    {
      //System.out.println("VMotion failed!");
      TaskInfo info = task.getTaskInfo();
      logger.info("Vmotion failed due to "+info.getError().getFault()+logger.getName());
      return false;
    }
	}
	 catch (InvalidProperty e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (RuntimeFault e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return false;
	}
	
	public boolean cloneVM(String cloneVMName, String vm_to_clone_name, String datacenterName )
	{
		 //String cloneName = "Pratik_VM_Cloned";
		   //String vm_to_clone_name = "Pratik_VM";
		   //String vmPath = "";
		   VirtualMachine vm_to_clone = null;
		   //String datacenterName= "DC-Team07"; // 
		   Folder rootFolder = serviceinstance.getRootFolder();
		   ManagedEntity[] virtual_machine_me;
		   int found =0;
		try {
			virtual_machine_me = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		
		
		if(virtual_machine_me==null || virtual_machine_me.length ==0)
		{
			logger.info("no virtual machine found");
			return false;
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
					found+=1;
				}
				
				
				
			}
			if(found == 0)
			{
				
					logger.info("Vm to be cloned is not found");
					return false;
				
			}
		}
	   
	  
		   
		   // get datacenter
		   
		   Datacenter dc = (Datacenter) serviceinstance.getSearchIndex().findByInventoryPath(datacenterName);
		   
		   if(dc ==null)
		   {
			   logger.info("VirtualMachine or Datacenter path is NOT correct. Pls double check. "+logger.getName());
			   return false;
		   }
		   Folder vmFolder = dc.getVmFolder();
	
		   VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
		   cloneSpec.setLocation(new VirtualMachineRelocateSpec());
		   cloneSpec.setPowerOn(false);
		   cloneSpec.setTemplate(false);

		   Task task = vm_to_clone.cloneVM_Task(vmFolder, cloneVMName, cloneSpec);
		   System.out.println("Launching the VM clone task. It might take a while. Please wait for the result ...");
		   
		   String status = 	task.waitForMe();
		   if(status==Task.SUCCESS)
		   {
	            logger.info("Virtual Machine got cloned successfully.");
	            return true;
		   }
		   else
		   {
			   System.out.println("Failure -: Virtual Machine cannot be cloned");
		   }
	   }
		catch (InvalidProperty e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
		return false;
		
	}
	
	public void createVM()
	{
		
	}
	
	public void removeVM()
	{
		
	}
	

}
