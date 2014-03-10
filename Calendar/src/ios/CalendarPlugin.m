#import "Calendar.h"
#import <Cordova/CDV.h>
#import <EventKitUI/EventKitUI.h>
#import <EventKit/EventKit.h>

@implementation Calendar
@synthesize eventStore;

- (CDVPlugin*) initWithWebView:(UIWebView*)theWebView {
    self = (Calendar*)[super initWithWebView:theWebView];
    if (self) {
        [self initEventStoreWithCalendarCapabilities];
    }
    return self;
}

- (void)initEventStoreWithCalendarCapabilities {
    __block BOOL accessGranted = NO;
    eventStore= [[EKEventStore alloc] init];
    if([eventStore respondsToSelector:@selector(requestAccessToEntityType:completion:)]) {
        dispatch_semaphore_t sema = dispatch_semaphore_create(0);
        [eventStore requestAccessToEntityType:EKEntityTypeEvent completion:^(BOOL granted, NSError *error) {
            accessGranted = granted;
            dispatch_semaphore_signal(sema);
        }];
        dispatch_semaphore_wait(sema, DISPATCH_TIME_FOREVER);
    } else { // we're on iOS 5 or older
        accessGranted = YES;
    }
    
    if (accessGranted) {
        self.eventStore = eventStore;
    }
}

- (void) createEventWithCalendar: (CDVInvokedUrlCommand*)command
                        calendar: (EKCalendar*) calendar {
    
    NSString *callbackId = command.callbackId;

    
    NSString *title = [command.arguments objectAtIndex:0];
    NSString *location = [command.arguments objectAtIndex:1];
    NSString *notes = [command.arguments objectAtIndex:2];
    NSNumber *startTime = [command.arguments objectAtIndex:3];
    NSNumber *endTime = [command.arguments objectAtIndex:4];
    
    NSTimeInterval _startInterval = [startTime doubleValue] / 1000;
    NSDate *myStartDate = [NSDate dateWithTimeIntervalSince1970:_startInterval];
    
    NSTimeInterval _endInterval = [endTime doubleValue] / 1000;
    
    EKEvent *myEvent = [EKEvent eventWithEventStore: self.eventStore];
    myEvent.title = title;
    myEvent.location = location;
    myEvent.notes = notes;
    myEvent.startDate = myStartDate;

    int duration = _endInterval - _startInterval;
    int moduloDay = duration % (60*60*24);
    if (moduloDay == 0) {
        myEvent.allDay = YES;
        myEvent.endDate = [NSDate dateWithTimeIntervalSince1970:_endInterval-1];
    } else {
        myEvent.endDate = [NSDate dateWithTimeIntervalSince1970:_endInterval];
    }
     
    myEvent.calendar = calendar;
    
    // TODO base on passed alarm with a decent default (1 hour)
    EKAlarm *reminder = [EKAlarm alarmWithRelativeOffset:-1*60*60];
    [myEvent addAlarm:reminder];
   
    NSError *error = nil;
    [self.eventStore saveEvent:myEvent span:EKSpanThisEvent error:&error];

    NSLog(@"%@", myEvent.eventIdentifier);
    if (error) {
        CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.userInfo.description];
        [self writeJavascript:[pluginResult toErrorCallbackString:callbackId]];
    } else {
       
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
    }
}
 
- (void)addCalendarEntry:(CDVInvokedUrlCommand*)command {
    EKCalendar* calendar = self.eventStore.defaultCalendarForNewEvents;
    [self createEventWithCalendar:command calendar:calendar];
}



-(void) deleteCalendarEntry:(CDVInvokedUrlCommand *)command
   {


    NSString *callbackId=command.callbackId;

    
    NSString *identifier = [command.arguments objectAtIndex:0];
    EKEvent* event= [eventStore eventWithIdentifier:identifier];
       NSError* error = nil;
       
       NSLog(@"%@", identifier);
    
       if(event!=nil)
    {
      [eventStore removeEvent:event span:EKSpanThisEvent error:&error];
        NSLog(@"Erfolg");
    }

    if(error){
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.userInfo.description];
        [self writeJavascript:[pluginResult toErrorCallbackString:callbackId]];
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
    }
   
}
-(void) editCalendarEntry:(CDVInvokedUrlCommand *) command
{
    
    NSString* callback=command.callbackId;
        
        NSString *identifier = [command.arguments objectAtIndex:0];
            EKEvent* event= [eventStore eventWithIdentifier:identifier];
if(event!=nil)
{
    
    NSString *newtitle = [command.arguments objectAtIndex:1];
    NSString *newlocation = [command.arguments objectAtIndex:2];
    NSString *newnotes = [command.arguments objectAtIndex:3];
    NSNumber *newstartTime = [command.arguments objectAtIndex:4];
    NSNumber *newendTime = [command.arguments objectAtIndex:5];
    
    
    if (newtitle) {
        event.title = newtitle;
    }
    if (newlocation) {
        event.location = newlocation;
    }
    if (newnotes) {
        event.notes = newnotes;
    }
    if (newstartTime) {
        NSTimeInterval _newstartInterval = [newstartTime doubleValue] / 1000; // strip millis
        event.startDate = [NSDate dateWithTimeIntervalSince1970:_newstartInterval];
    }
    if (newendTime) {
        NSTimeInterval _newendInterval = [newendTime doubleValue] / 1000; // strip millis
        event.endDate = [NSDate dateWithTimeIntervalSince1970:_newendInterval];
    }
    
    // Now save the new details back to the store
    NSError *error = nil;
    [self.eventStore saveEvent:event span:EKSpanThisEvent error:&error];
    
    // Check error code + return result
    if (error) {
        CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.userInfo.description];
        [self writeJavascript:[pluginResult toErrorCallbackString:callback]];
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self writeJavascript:[pluginResult toSuccessCallbackString:callback]];
    }
  


}
   
}


-(NSString*) searchCalendarEntryId:(CDVInvokedUrlCommand*) command
               {

                   NSString *title = [command.arguments objectAtIndex:0];
                   NSString *location = [command.arguments objectAtIndex:1];
                   NSString *notes = [command.arguments objectAtIndex:2];
                   NSNumber *startTime = [command.arguments objectAtIndex:3];
                   NSNumber *endTime = [command.arguments objectAtIndex:4];
                   
                   NSLog(@"Title  %@",title);
                   NSLog(@"Location %@",location);
                   NSLog(@"Notes %@",notes);
                   
                   
                   NSTimeInterval startInterval = [startTime doubleValue] / 1000;
                   NSDate *startDate = [NSDate dateWithTimeIntervalSince1970:startInterval];
                   
                   NSTimeInterval endInterval = [endTime doubleValue] / 1000;
                   NSDate *endDate = [NSDate dateWithTimeIntervalSince1970:endInterval];
                   
                   
    EKCalendar* calendar = self.eventStore.defaultCalendarForNewEvents;
    NSMutableString *predicateString = [[NSMutableString alloc] initWithString:@""];
        NSLog(@"VOr forschleife");
    //if (![title isEqualToString:@"null"]) {
        [predicateString appendString:[NSString stringWithFormat:@"title == '%@'", title]];
    //}
    //if (![location isEqualToString:@"null"]) {
        [predicateString appendString:[NSString stringWithFormat:@" AND location == '%@'", location]];
    //}
   // if (![notes isEqualToString:@"null"]) {
        [predicateString appendString:[NSString stringWithFormat:@" AND notes == '%@'", notes]];
    //}
                        NSLog(@"Nach forschleife");
                   /*
                   if ([title isEqualToString:@"NULL"]) {
                       [predicateString appendString:[NSString stringWithFormat:@"title == '%@'", title]];
                   }
                   if ([location isEqualToString:@"NULL"]) {
                       [predicateString appendString:[NSString stringWithFormat:@" AND location == '%@'", location]];
                   }
                   if ([notes isEqualToString:@"NULL"]) {
                       [predicateString appendString:[NSString stringWithFormat:@" AND notes == '%@'", notes]];
                   }
    */
    
    NSPredicate *matches = [NSPredicate predicateWithFormat:predicateString];
    
    NSArray *calendarArray = [NSArray arrayWithObject:calendar];
    
    NSArray *datedEvents = [self.eventStore eventsMatchingPredicate:[eventStore predicateForEventsWithStartDate:startDate endDate:endDate calendars:calendarArray]];
     //  NSArray *matchingEvents = [self.eventStore eventsMatchingPredicate:matches];
    NSArray *matchingEvents = [datedEvents filteredArrayUsingPredicate:matches];
    
   NSMutableArray *matchingId= [NSMutableArray arrayWithCapacity:[matchingEvents count]];
     NSLog(@"Nach filter");
    for(EKEvent *event in matchingEvents){
        [matchingId addObject:event.eventIdentifier];
    }
     
   
                   NSString *json= [matchingId JSONString];
              NSString *callbackId = command.callbackId;
               
                       CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:json];
                       [self writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
                   NSLog(@"%@",json);
                   
                   
                   
    return json;
}




@end
