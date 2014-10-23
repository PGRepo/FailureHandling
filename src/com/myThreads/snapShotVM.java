package com.myThreads;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.controller.host.VMHost;
import com.controller.vm.VirtualMachineController;
import com.vmware.scia.utils.ObjectUtils;
import com.vmware.vim25.FileFault;
import com.vmware.vim25.HostNotConnected;
import com.vmware.vim25.InsufficientResourcesFault;
import com.vmware.vim25.InvalidName;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SnapshotFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.VirtualMachineSnapshotInfo;
import com.vmware.vim25.VirtualMachineSnapshotTree;
import com.vmware.vim25.VmConfigFault;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.VirtualMachineSnapshot;

public class snapShotVM implements Runnable{
	ServiceInstance serviceinstance_vm;
	ServiceInstance serviceinstance_host;
	List<VirtualMachine> listOfAvailableVm = new ArrayList<VirtualMachine>();
	List<HostSystem> listOfMyHost = new ArrayList<HostSystem>();
	List<VirtualMachine> listOfAllHost= new ArrayList<VirtualMachine>();
	List<VirtualMachine> listOfHostToTakeSnapshot = new ArrayList<VirtualMachine>(); 
	
	VirtualMachineController vmc;
	VMHost vmh;
	
	public snapShotVM(ServiceInstance si_v, ServiceInstance si_h) 
	{
		
		// TODO Auto-generated constructor stub
		
		serviceinstance_vm = si_v;
		serviceinstance_host = si_h;
		vmc = new VirtualMachineController();
		vmh=new VMHost();
		
		
		//List<VirtualMachine> listOfVirtualMachine = vmc.getAllVM();
		
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String operation = "create";
		while(true)
		{
			
		try {
			
			listOfAvailableVm = vmc.getAllVM(serviceinstance_vm);
			HostSystem hs;
			
			for(VirtualMachine vm : listOfAvailableVm)
			{
				
				//HostSystem hs = new HostSystem();
				String host_ip = vm.getResourcePool().getParent().getName();
				ManagedEntity hostmanagedEntity = new InventoryNavigator(serviceinstance_vm.getRootFolder()).searchManagedEntity("HostSystem", host_ip);				
				hs = (HostSystem)hostmanagedEntity;
				
				boolean host_connected=vmh.checkHostConnectivity(hs);
				
				//List<HostSystem> listOfHost = vmh.getAllHost(serviceinstance_vm);
				/*for(HostSystem hs: listOfHost)
				{
					if(hs.getName().equalsIgnoreCase(host_ip))
							{
								VMHost hostcontroller = new VMHost();
								if(!hostcontroller.checkHostConnectivity(hs))
								{
									System.out.println("Host seems down ....");
									System.out.println("Avaoiding taking snapshot...");
									continue;
								}
								
							}
				}*/
				
				if(!host_connected)
				{
					continue;
				}
				
				
					if(vmc.removeAllSnapShots(vm))
					{
						if(vmc.createSnapShot(vm))
						{
							System.out.println("Snapshot created for "+ vm.getName());
						}
					}
					else
						{
							System.out.println("Failed to remove snapshot...");
						}
				
				
//				else
//				{
//					System.out.println(vm.getName()+" is powered off");
//					continue;
//				}
				
			
			}
			
			System.out.println("Sleep....");
			Thread.sleep(420000);
			System.out.println("start snapshot for hosts....");
			// log into vcenter admin and take snapshot of these host
			snapshotHost();
			Thread.sleep(42000);
			System.out.println("start snapshot for vms....");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("Interrupted Exception.....executed");
			e.printStackTrace();
		}catch (Exception e)
		{
			System.out.println("Exception.....executed");
			e.printStackTrace();
		}
		
		
		}
		
	}
	
	public void snapshotHost()
	{
		VMHost vmh = new VMHost();
		// get list of all host as from vm_client
		List<HostSystem> host_list=vmh.getAllHost(serviceinstance_vm);
		List<VirtualMachine> a =new ArrayList<VirtualMachine>();
		// get all host names from vcenter admin
		List<VirtualMachine> listofHostFromAdmin = vmc.getAllVM(serviceinstance_host);
		for(VirtualMachine vm: listofHostFromAdmin)
		{
			try{
				
				
					if(vm!= null && vm.getResourcePool() != null && vm.getResourcePool().getName().equalsIgnoreCase("Team07_vHosts"))
						{
					
					
							System.out.println(vm.getResourcePool().getName());
							System.out.println(vm.getName());
							if(vmc.removeAllSnapShots(vm))
							{
								if(vmc.createSnapShot(vm))
								{
									System.out.println("Snapshot created for "+ vm.getName());
								}
							}
							else
								{
									System.out.println("Failed to remove snapshot...");
								}
						
							
							// start snapshot process for this host
							// skip snapshot if vm is powered off
							
						}
				} 
			catch ( RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(NullPointerException e)
			{
				System.out.println("catched exception");
				continue;
			}
		}
			
			
	}
		
}
	


	
	
	


