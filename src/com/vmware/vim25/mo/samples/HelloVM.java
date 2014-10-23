/*================================================================================
Copyright (c) 2008 VMware, Inc. All Rights Reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
this list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.

* Neither the name of VMware, Inc. nor the names of its contributors may be used
to endorse or promote products derived from this software without specific prior 
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL VMWARE, INC. OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.
================================================================================*/

package com.vmware.vim25.mo.samples;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

import com.controller.vm.VirtualMachineController;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;
import com.vmware.vim25.mo.util.OptionSpec;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;




public class HelloVM 
{
	public static void main(String[] args) throws Exception
	{
		

        /*
         *  This block configure the logger with handler and formatter
         */
		final Logger logger = Logger.getLogger(HelloVM.class.getName());
	    FileHandler fh;  
	    try 
	    { 
	        fh = new FileHandler("E:/workspace/DisasterRecovery/logs/MyLogFile.log");  
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
	    catch (IOException e) 
	    {  
	        e.printStackTrace();  
	    } 
		
		
	    /*
		 * Fetch values from the property file
		 */
	    
		String _vcenter_url = null;
		String _username = null;
		String _password = null;
		
		try (FileReader reader = new FileReader("config/config_details.properties")) 
		{
			Properties properties = new Properties();
			properties.load(reader);
			_vcenter_url = properties.getProperty("vCenter_url");
			_username = properties.getProperty("username");
			_password = properties.getProperty("password");
			
		} 
		catch (IOException e) 
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
		
				
		// Get the details for host in vcenter
		//process each host and call getVMDetails function to get details of all host
		
		ManagedEntity[] hostmanagedEntities = new InventoryNavigator(
				serviceinstance.getRootFolder()).searchManagedEntities("HostSystem");
		HostSystem host = null;
		

		logger.info("Print V HOST"+logger.getName());
		
		System.out.println("################# All HOST IN VCENTER and all VM in HOST ARE ##############");
		for (ManagedEntity managedEntity : hostmanagedEntities) 
		{
			System.out.println();
			host = (HostSystem)managedEntity;
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
				int i=1;
				System.out.println("######################## Details for Virtual Machine ###################");
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
						System.out.println(v.getGuest().getGuestId());
						logger.info("checking for the power status...");
						
						/*if(v.getSummary().runtime.powerState.toString().equals("poweredOn"))
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
						}*/
						
						i++;
				}
			}
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
	
	    boolean b =false;
if (b)
{
		
    System.out.println("############################ Clone Virtual Machine Begines #################");
    
    
	   String cloneName = "Pratik_VM_Cloned";
	   String vm_to_clone_name = "Pratik_VM";
	   //String vmPath = "";
	   VirtualMachine vm_to_clone = null;
	   String datacenterName= "DC-Team07"; // 
	   
	   
	   // get vm to be cloned
	  
	   ManagedEntity[] virtual_machine_me = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		int found = 0;
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
					found +=1;
				}
			}
			if(found ==0){
				logger.info("Vm to be cloned is not found");
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
}
	  /* catch(MalformedURLException mue)
	   {
		   mue.printStackTrace();
	   }*/
	   
	   
	   
	   /*
	   
	  System.out.println("############################ Create VM ########################");

  
    String dcName = "DC-Team07";
    String hostName= "130.65.132.192";
    String vmName = "Pratik_VM_Created";
    long memorySizeMB = 1024;
    int cupCount = 1;
    //String guestOsId = "sles10Guest";
    String guestOsId = "UbuntuGuestId";
    long diskSizeKB = 8000000;
    // mode: persistent|independent_persistent,
    // independent_nonpersistent
    String diskMode = "persistent";
    String datastoreName = "nfs1team07";
    String netName = "VM Network";
    String nicName = "Network Adapter 1";
    HostSystem hs = (HostSystem) new InventoryNavigator(
            rootFolder).searchManagedEntity("HostSystem", hostName);
    
    Datacenter dc = (Datacenter) new InventoryNavigator(
        rootFolder).searchManagedEntity("Datacenter", dcName);
    ResourcePool rp = (ResourcePool) new InventoryNavigator(
        dc).searchManagedEntities("ResourcePool")[0];
    
    Folder vmFolder = dc.getVmFolder();
    

    // create vm config spec
    VirtualMachineConfigSpec vmSpec = 
      new VirtualMachineConfigSpec();
    vmSpec.setName(vmName);
    vmSpec.setAnnotation("VirtualMachine Annotation");
    vmSpec.setMemoryMB(memorySizeMB);
    vmSpec.setNumCPUs(cupCount);
    vmSpec.setGuestId(guestOsId);

    // create virtual devices
    int cKey = 1000;
    VirtualDeviceConfigSpec scsiSpec = createScsiSpec(cKey);
    VirtualDeviceConfigSpec diskSpec = createDiskSpec(
        datastoreName, cKey, diskSizeKB, diskMode);
    VirtualDeviceConfigSpec nicSpec = createNicSpec(
        netName, nicName);

    vmSpec.setDeviceChange(new VirtualDeviceConfigSpec[] 
        {scsiSpec, diskSpec, nicSpec});
    
    // create vm file info for the vmx file
    VirtualMachineFileInfo vmfi = new VirtualMachineFileInfo();
    vmfi.setVmPathName("["+ datastoreName +"]");
    vmSpec.setFiles(vmfi);

    // call the createVM_Task method on the vm folder
    Task task = vmFolder.createVM_Task(vmSpec, rp, hs);
    String result = task.waitForMe();       
    if(result == Task.SUCCESS) 
    {
      System.out.println("VM Created Sucessfully");
    }
    else 
    {
      System.out.println("VM could not be created. ");
    }
	serviceinstance.getServerConnection().logout();
	
	
	
	}
	

  static VirtualDeviceConfigSpec createScsiSpec(int cKey)
  {
    VirtualDeviceConfigSpec scsiSpec = 
      new VirtualDeviceConfigSpec();
    scsiSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
    VirtualLsiLogicController scsiCtrl = 
        new VirtualLsiLogicController();
    scsiCtrl.setKey(cKey);
    scsiCtrl.setBusNumber(0);
    scsiCtrl.setSharedBus(VirtualSCSISharing.noSharing);
    scsiSpec.setDevice(scsiCtrl);
    return scsiSpec;
  }
  
  static VirtualDeviceConfigSpec createDiskSpec(String dsName, 
      int cKey, long diskSizeKB, String diskMode)
  {
    VirtualDeviceConfigSpec diskSpec = 
        new VirtualDeviceConfigSpec();
    diskSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
    diskSpec.setFileOperation(
        VirtualDeviceConfigSpecFileOperation.create);
    
    VirtualDisk vd = new VirtualDisk();
    vd.setCapacityInKB(diskSizeKB);
    diskSpec.setDevice(vd);
    vd.setKey(0);
    vd.setUnitNumber(0);
    vd.setControllerKey(cKey);

    VirtualDiskFlatVer2BackingInfo diskfileBacking = 
        new VirtualDiskFlatVer2BackingInfo();
    String fileName = "["+ dsName +"]";
    diskfileBacking.setFileName(fileName);
    diskfileBacking.setDiskMode(diskMode);
    diskfileBacking.setThinProvisioned(true);
    vd.setBacking(diskfileBacking);
    return diskSpec;
  }
  
  static VirtualDeviceConfigSpec createNicSpec(String netName, 
      String nicName) throws Exception
  {
    VirtualDeviceConfigSpec nicSpec = 
        new VirtualDeviceConfigSpec();
    nicSpec.setOperation(VirtualDeviceConfigSpecOperation.add);

    VirtualEthernetCard nic =  new VirtualPCNet32();
    VirtualEthernetCardNetworkBackingInfo nicBacking = 
        new VirtualEthernetCardNetworkBackingInfo();
    nicBacking.setDeviceName(netName);

    Description info = new Description();
    info.setLabel(nicName);
    info.setSummary(netName);
    nic.setDeviceInfo(info);
    
    // type: "generated", "manual", "assigned" by VC
    nic.setAddressType("generated");
    nic.setBacking(nicBacking);
    nic.setKey(0);
   
    nicSpec.setDevice(nic);
    return nicSpec;*/


// Create snapshot of vm

/*
================================================================================*/
InetAddress address = InetAddress.getByName("130.65.132.192");
if(address.isReachable(100000))
	{
	System.out.println("reached");
	}
else
{
	System.out.println("not reachable");
}





    //String vmname = args[3];
    //String op = args[4];
    String operation = "create";
    String vmName= "Pratik_VM";
    
    
    //please change the following three depending your op
    String snapshotname = "SanpShot"+"_"+vmName;
    String desc = "SnapShot created for vm. this is description";
    boolean removechild = true;
    
    

    //Folder rootFolder = si.getRootFolder();
    VirtualMachine vm = (VirtualMachine) new InventoryNavigator(
      rootFolder).searchManagedEntity("VirtualMachine", vmName);

    if(vm==null)
    {
      System.out.println("No VM " + vmName + " found");
      serviceinstance.getServerConnection().logout();
      return;
    }

    if("create".equalsIgnoreCase(operation))
    {
      Task task = vm.createSnapshot_Task(
          snapshotname, desc, false, false);
      if(task.waitForMe()==Task.SUCCESS)
      {
        System.out.println("Snapshot was created.");
      }
    }
    else if("list".equalsIgnoreCase(operation))
    {
      listSnapshots(vm);
    }
    else if(operation.equalsIgnoreCase("revert")) 
    {
      VirtualMachineSnapshot vmsnap = getSnapshotInTree(
          vm, snapshotname);
      if(vmsnap!=null)
      {
        Task task = vmsnap.revertToSnapshot_Task(null);
        if(task.waitForMe()==Task.SUCCESS)
        {
          System.out.println("Reverted to snapshot:" 
              + snapshotname);
        }
      }
    }
    else if(operation.equalsIgnoreCase("removeall")) 
    {
      Task task = vm.removeAllSnapshots_Task();      
      if(task.waitForMe()== Task.SUCCESS) 
      {
        System.out.println("Removed all snapshots");
      }
    }
    else if(operation.equalsIgnoreCase("remove")) 
    {
      VirtualMachineSnapshot vmsnap = getSnapshotInTree(
          vm, snapshotname);
      if(vmsnap!=null)
      {
        Task task = vmsnap.removeSnapshot_Task(removechild);
        if(task.waitForMe()==Task.SUCCESS)
        {
          System.out.println("Removed snapshot:" + snapshotname);
        }
      }
    }
    else 
    {
      System.out.println("Invalid operation");
      return;
    }
    serviceinstance.getServerConnection().logout();
  }
  
  static VirtualMachineSnapshot getSnapshotInTree(
      VirtualMachine vm, String snapName)
  {
    if (vm == null || snapName == null) 
    {
      return null;
    }

    VirtualMachineSnapshotTree[] snapTree = 
        vm.getSnapshot().getRootSnapshotList();
    if(snapTree!=null)
    {
      ManagedObjectReference mor = findSnapshotInTree(
          snapTree, snapName);
      if(mor!=null)
      {
        return new VirtualMachineSnapshot(
            vm.getServerConnection(), mor);
      }
    }
    return null;
  }

  static ManagedObjectReference findSnapshotInTree(
      VirtualMachineSnapshotTree[] snapTree, String snapName)
  {
    for(int i=0; i <snapTree.length; i++) 
    {
      VirtualMachineSnapshotTree node = snapTree[i];
      if(snapName.equals(node.getName()))
      {
        return node.getSnapshot();
      } 
      else 
      {
        VirtualMachineSnapshotTree[] childTree = 
            node.getChildSnapshotList();
        if(childTree!=null)
        {
          ManagedObjectReference mor = findSnapshotInTree(
              childTree, snapName);
          if(mor!=null)
          {
            return mor;
          }
        }
      }
    }
    return null;
  }

  static void listSnapshots(VirtualMachine vm)
  {
    if(vm==null)
    {
      return;
    }
    VirtualMachineSnapshotInfo snapInfo = vm.getSnapshot();
    VirtualMachineSnapshotTree[] snapTree = 
      snapInfo.getRootSnapshotList();
    printSnapshots(snapTree);
  }

  static void printSnapshots(
      VirtualMachineSnapshotTree[] snapTree)
  {
    for (int i = 0; snapTree!=null && i < snapTree.length; i++) 
    {
      VirtualMachineSnapshotTree node = snapTree[i];
      System.out.println("Snapshot Name : " + node.getName());           
      VirtualMachineSnapshotTree[] childTree = 
        node.getChildSnapshotList();
      if(childTree!=null)
      {
        printSnapshots(childTree);
      }
    }
 






  
	}
	
  }

	   
	   
	   
	
		
		

