Telegram bot @gcalendar_reminder_bot for receive events from google calendar api. All events send at 9.00 PM MSK every day.
***
**Usage:**

1) **/register** - need to pass key for calendar access. It's google service account, and it usually looks like some
   text file in json format. You may register this account using
   this [link](https://cloud.google.com/iam/docs/creating-managing-service-accounts).
2) **/calendar** - need to pass calendar id which you want to subscribe for receive events.
   Service account must have access to this calendar. It's maybe greater than one.
3) **/delete** - need to pass some calendar name for unsubscribe.
4) **/unregister** - for delete all user's data like calendars and access key.
5) **/help** - for help message.
