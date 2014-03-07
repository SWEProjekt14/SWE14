#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>
#import <EventKitUI/EventKitUI.h>
#import <EventKit/EventKit.h>

@interface CalendarPlugin : CDVPlugin

@property (nonatomic, retain) EKEventStore* eventStore;

- (void) createEvent:(CDVInvokedUrlCommand*) command;
- (void) deleteEvent:(CDVInvokedUrlCommand*) command;
-(void) modifyEvent:(CDVInvokedUrlCommand *) command;
-(NSString*) searchEvent:(CDVInvokedUrlCommand *) command;
@end