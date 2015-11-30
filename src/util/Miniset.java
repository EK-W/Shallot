package util;


public class Miniset<E> {
	Object[] array;
	public Miniset(){
		array = new Object[0];
	}
	public Miniset(E... elements){
		array =	elements;
	}
	public E get(int index){
		return (E) array[index];
	}
	public int getIndex(E element){
		for(int i = 0; i < array.length; i++){
			if(element.equals(array[i])){
				return i;
			}
		}
		return -1;
	}
	public int size(){
		return array.length;
	}
	public void set(E...es){
		array = es;
	}
	public void add(E toAdd){
		Object[] temp = new Object[array.length + 1];
		System.arraycopy(array, 0, temp, 0, array.length);
		temp[array.length] = toAdd;
		array = temp;
	}
	public void add(E toAdd, int index){
		Object[] temp = new Object[array.length + 1];
		for(int i = 0; i < index; i++){
			temp[i] = array[i];
		}
		temp[index] = toAdd;
		for(int i = index; i < array.length; i++){
			temp[i + 1] = array[i];
		}
		array = temp;
	}
}
