using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using WPCordovaClassLib.Cordova;
using WPCordovaClassLib.Cordova.Commands;
using WPCordovaClassLib.Cordova.JSON;

using Microsoft.Phone.Tasks;

namespace WPCordovaClassLib.Cordova.Commands
{
    class Calendar : BaseCommand
    { 
         
        public void addCalendarEntryInteractive(string options)
        {
            string[] args = JsonHelper.Deserialize<string[]>(options);
            long timestampStart = Convert.ToInt64(args[3]);
            long timestampEnd = Convert.ToInt64(args[4]);
            DateTime dtRef = new DateTime(1970, 1, 1);
            SaveAppointmentTask saveTask = new SaveAppointmentTask();
            saveTask.StartTime = new DateTime(dtRef.Ticks + 10000 * timestampStart).ToLocalTime();
            saveTask.EndTime = new DateTime(dtRef.Ticks + 10000 * timestampEnd).ToLocalTime();
            saveTask.Location= args[2];
            saveTask.Details= args[1];
            saveTask.Subject = args[0];

            saveTask.Show();

            this.DispatchCommandResult(new PluginResult(PluginResult.Status.OK));

        }

        public void addCalendarEntry(string options)
        {
            string[] args = JsonHelper.Deserialize<string[]>(options);
            
            this.DispatchCommandResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
        }
        
        public void editCalendarEntry(string options)
        {
            string[] args = JsonHelper.Deserialize<string[]>(options);
            
            this.DispatchCommandResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
        }
        
        public void deleteCalendarEntry(string options)
        {
            string[] args = JsonHelper.Deserialize<string[]>(options);
            
            this.DispatchCommandResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
        }
        
        public void searchCalendarEntry(string options)
        {
            string[] args = JsonHelper.Deserialize<string[]>(options);
            
            this.DispatchCommandResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
        }

        public void searchCalendarEntryId(string options)
        {
            string[] args = JsonHelper.Deserialize<string[]>(options);

            this.DispatchCommandResult(new PluginResult(PluginResult.Status.INVALID_ACTION));

        }
    }
}
