package entities;

public interface Entity {
	//The mult is to allow for a non fixed framerate.
	void onUpdate(double mult);
}
