#import "CalendarPlugin.h"
#import <Cordova/CDV.h>
#import <EventKitUI/EventKitUI.h>
#import <EventKit/EventKit.h>

@implementation CalendarPlugin
@synthesize eventStore;

- (CDVPlugin*) initWithWebView:(UIWebView*)theWebView {
    self = (CalendarPlugin*)[super initWithWebView:theWebView];
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
    NSDictionary *options = [command.arguments objectAtIndex:0];
    
    NSString *title = [options objectForKey: @"title"];
    NSString *location = [options objectForKey: @"location"];
    NSString *notes = [options objectForKey: @"notes"];
    NSNumber *startTime = [options objectForKey: @"startTime"];
    NSNumber *endTime = [options objectForKey: @"endTime"];
    
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

    if (error) {
        CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.userInfo.description];
        [self writeJavascript:[pluginResult toErrorCallbackString:callbackId]];
    } else {
       
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
    }
}
 
- (void)createEvent:(CDVInvokedUrlCommand*)command {
    EKCalendar* calendar = self.eventStore.defaultCalendarForNewEvents;
    [self createEventWithCalendar:command calendar:calendar];
}



-(void) deleteEvent:(CDVInvokedUrlCommand *)command
   {


    NSString *callbackId=command.callbackId;
    NSDictionary *options = [command.arguments objectAtIndex:0];
    
    NSString *identifier = [options objectForKey:@"identifier"];
    EKEvent* event= [eventStore eventWithIdentifier:identifier];
    NSError* error = nil;
    if(event!=nil)
    {
        
      [eventStore removeEvent:event span:EKSpanThisEvent error:&error];
    }

    if(error){
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.userInfo.description];
        [self writeJavascript:[pluginResult toErrorCallbackString:callbackId]];
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
    }
   
}
-(void) modifyEvent:(CDVInvokedUrlCommand *) command
{
    
    NSString* callback=command.callbackId;
    NSDictionary *options = [command.arguments objectAtIndex:0];
        
        NSString *identifier = [options objectForKey:@"identifier"];
            EKEvent* event= [eventStore eventWithIdentifier:identifier];
if(event!=nil)
{
    NSString* newtitle     = [options objectForKey:@"title"];
    NSString* newlocation  = [options objectForKey:@"location"];
    NSString* newnotes     = [options objectForKey:@"notes"];
    NSNumber* newstartTime = [options objectForKey:@"startTime"];
    NSNumber* newendTime   = [options objectForKey:@"endTime"];
    
    
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


-(NSString*) searchEvent:(CDVInvokedUrlCommand*) command
               {

    NSDictionary *options = [command.arguments objectAtIndex:0];
    
    NSString *title = [options objectForKey:@"title"];
    NSString *location = [options objectForKey:@"location"];
    NSString *notes = [options objectForKey:@"notes"];
    NSNumber *startTime = [options objectForKey:@"startTime"];
    NSNumber *endTime = [options objectForKey:@"endTime"];
  
                   NSTimeInterval startInterval = [startTime doubleValue] / 1000;
                   NSDate *startDate = [NSDate dateWithTimeIntervalSince1970:startInterval];
                   
                   NSTimeInterval endInterval = [endTime doubleValue] / 1000;
                   NSDate *endDate = [NSDate dateWithTimeIntervalSince1970:endInterval];
    EKCalendar* calendar = self.eventStore.defaultCalendarForNewEvents;
    NSMutableString *predicateString = [[NSMutableString alloc] initWithString:@""];
   
    if (title.length > 0) {
        [predicateString appendString:[NSString stringWithFormat:@"title == '%@'", title]];
    }
    if (location.length > 0) {
        [predicateString appendString:[NSString stringWithFormat:@" AND location == '%@'", location]];
    }
    if (notes.length > 0) {
        [predicateString appendString:[NSString stringWithFormat:@" AND notes == '%@'", notes]];
    }
    
    
    NSPredicate *matches = [NSPredicate predicateWithFormat:predicateString];
    
    NSArray *calendarArray = [NSArray arrayWithObject:calendar];
    
    NSArray *datedEvents = [self.eventStore eventsMatchingPredicate:[eventStore predicateForEventsWithStartDate:startDate endDate:endDate calendars:calendarArray]];
    
    NSArray *matchingEvents = [datedEvents filteredArrayUsingPredicate:matches];
    
   NSMutableArray *matchingId= [NSMutableArray arrayWithCapacity:[matchingEvents count]];

    for(EKEvent *event in matchingEvents){
        [matchingId addObject:event.eventIdentifier];
    }
     
   
                   NSString *json= [matchingId JSONString];
            
    return json;
}




@end