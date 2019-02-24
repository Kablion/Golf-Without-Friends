Maps werden gespeichert unter: C:/Benutzer/*NAME*/Golf Without Friends/
Maps werden alle 5 Minuten automatisch gespeichert (autosaved.json wird überschrieben)
Eine gespeicherte Map kann theoretisch direkt im Game getestet werden, weil sie von dem selben Ort aus geladen werden.
Ball kann nicht hinzugefügt werden (ist noch ein Bug, dass es gelistet wird)
Tool um Welt zu erstellen muss noch gemacht werden
Manchmal können veränderungen nur angezeigt werden, wenn "Show Changes" geklickt wird

Zoom mit Mausrad
verschieben mit Mittlerer Maustaste halten

-Entity List: 
-je weiter oben eine Entity steht, desto eher ist sie zu sehen. Z.B. Ball ist immer ganz oben und ist immer über allem anderen zu sehen.


Camera Settings:
- Gibts glaube ich nicht viel zu zu sagen

Ground:
-die Boden Textur kann mit TextureOffset verschoben werden, um zwei Grounds nahtlos aussehen zu lassen
Vertices sind die Eckpunkte von der Position aus gesehen (ein Vertices 0,0 ist nicht auf 0,0 sondern auf positionX,positionY von der Entity): 
-bei nur einem ist es ein kreis mit radius (Abstand zum 0,0)
-bei 2 ist es ein Rechteck
-bei 3 oder mehr wird es ein Polygon
-das Polygon wird der Reihenach aufgespannt, deswegen kann man die vertices nach oben oder unten verschieben.

Wall:
-wenn isCircle angekreuzt, zählt die Length als Radius
-entweder es wird eine Position, eine Länge und ein Winkel angegeben
-oder es wird eine Position und eine Zielposition gegeben (die Wall ist dann von der Position zum toPosition aufgestellt)

Hole:
-es kann auch mehr als 1 Hole pro Map geben

Magnet:
-range bestimmt den Radius
-strength bestimmt wie stark der Ball in die Mitte gezogen wird