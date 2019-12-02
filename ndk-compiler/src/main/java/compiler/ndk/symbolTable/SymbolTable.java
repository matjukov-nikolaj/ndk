package compiler.ndk.symbolTable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import compiler.ndk.ast.blockElems.declarations.Declaration;
import compiler.ndk.ast.blockElems.declarations.VarDec;

public class SymbolTable {

	static class SymbolTableEntry {
		SymbolTableEntry(int scope, VarDec dec,
						 SymbolTableEntry next) {
			super();
			this.scope = scope;
			this.dec = dec;
			this.next = next;
		}

		int scope;
		VarDec dec;
		SymbolTableEntry next;
	}

	private HashMap<String, SymbolTableEntry> entries;
	private LinkedList<Integer> scopeStack;
	private int currentScope;


	public int enterScope() {
		scopeStack.addFirst(++currentScope);
		return scopeStack.size();
	}

	public int leaveScope() {
		int size = scopeStack.size();
		if (size > 0) scopeStack.removeFirst();
		return size;
	}

	public boolean insert(String ident, VarDec dec) {
		SymbolTableEntry entry = entries.get(ident);
		while (entry != null) {
			if (entry.scope == currentScope) {
				return false;
			}
			entry = entry.next;
		}
		entries.put(ident, new SymbolTableEntry(currentScope, dec, entries.get(ident)));
		return true;
	}

	public Declaration lookup(String ident) {
		SymbolTableEntry entry = entries.get(ident);
		if (entry == null) return null;
		SymbolTableEntry tmpEntry;
		for (int i = 0; i < scopeStack.size(); ++i) {
			tmpEntry = entry;
			int scope = scopeStack.get(i);
			while (tmpEntry != null && tmpEntry.scope != scope) {
				tmpEntry = tmpEntry.next;
			}
			if (tmpEntry != null) return tmpEntry.dec;
		}
		return null;
	}

	public SymbolTable() {
		currentScope = 0;
		entries = new HashMap<>();
		scopeStack = new LinkedList<>();
		scopeStack.addFirst(currentScope);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("scope stack: (top first)\n");
		for (int scopeNum : scopeStack) {
			sb.append(scopeNum).append('\n');
		}
		sb.append("entries:\n");
		Set<Entry<String, SymbolTableEntry>> mapEntrySet = entries.entrySet();
		for (Entry<String, SymbolTableEntry> mapEntry : mapEntrySet) {
			sb.append(mapEntry.getKey()).append(':');
			SymbolTableEntry entry = mapEntry.getValue();
			while (entry != null) {
				sb.append('[').append(entry.scope).append(',').append(entry.dec.toString()).append("] ");
				entry = entry.next;
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}
