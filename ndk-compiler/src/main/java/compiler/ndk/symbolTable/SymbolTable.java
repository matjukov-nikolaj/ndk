package compiler.ndk.symbolTable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import compiler.ndk.ast.blockElems.declarations.Declaration;

public class SymbolTable {
	
	static class SymbolTableEntry{
		public SymbolTableEntry(int scope, Declaration dec,
				SymbolTableEntry next) {
			super();
			this.scope = scope;
			this.dec = dec;
			this.next = next;
		}
		int scope;
		Declaration dec;
		SymbolTableEntry next;
	}

	HashMap<String, SymbolTableEntry> entries;
	LinkedList<Integer> scopeStack;
	int currentScope;
	
	public SymbolTable() {
		currentScope = 0;
		entries = new HashMap<>();
		scopeStack = new LinkedList<>();
		scopeStack.addFirst(currentScope); //push
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("scope stack: (top first)\n");
		for (int scopeNum: scopeStack){
			sb.append(scopeNum).append('\n');
		}
		sb.append("entries:\n");
		Set<Entry<String, SymbolTableEntry>>  mapEntrySet = entries.entrySet();
		for (Entry<String, SymbolTableEntry> mapEntry: mapEntrySet){
			sb.append(mapEntry.getKey()).append(':');
			SymbolTableEntry entry = mapEntry.getValue();
			while (entry != null){
				sb.append('[').append(entry.scope).append(',').append(entry.dec.toString()).append("] ");
				entry = entry.next;
			}
			sb.append('\n');
		}
		return sb.toString();
	}

}
