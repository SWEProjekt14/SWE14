Die index.html liegt im Projektordner unter 
<ProjektName>\platforms\android\assets\www
Ich habe einen Button hinzugefügt, der einen Text an das Plugin weiterleitet, und die Rückgabe in ein vorgesetztes div schreibt. Eine eigene Seite mit Textfeld war mir jetzt erstmal zu viel, das reicht ja um das Plugin zu testen.

Um das Plugin zu installieren, habe ich den plugman verwendet (npm install -g plugman):
 	plugman install --platform android --project <PfadZurApp>\platforms\android --plugin C:\Users\Pierre\Plugins\HelloWorld
Heißt hat alles ohne Git oder sonst was funktioniert. 
Ich empfehle sehr den Emulator dann unter ADT (Eclipse) zu starten, da es dort ein Log gibt, das Fehler und Meldungen der App mitlogt. Es wird von verschiedenen scheinbar/hoffentlich unwichtigen Meldungen zugespamt, aber Fehler vom eigenen Code kann man darüber auch gut finden.

Natürlich gibt es verschiedene Möglichkeiten, so ein JavaScript interface zu schreiben, aber so hat es halt für mich funktioniert. Was die verschiedenen Werte in der plugin.xml machen weiss ich auch noch nicht genau. Aber um das Plugin wieder zu deinstallieren (um danach eine neuere Version zu installieren) habe ich den Befehl verwendet:
 	plugman uninstall --platform android --project <ProjektPfad>\platforms\android --plugin com.example.hello