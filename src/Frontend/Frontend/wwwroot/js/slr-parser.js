function extend(objekt, zuper) {
    _.extend(objekt, zuper);

    objekt.zuper = zuper;
}

function newObject(prototype) {
    function F() {
        // Deliberatley left empty
    }

    F.prototype = prototype;

    return new F();
}

function includes(array1, array2) {
    for (var i in array1) {
        if (array2.indexOf(array1[i]) < 0) {
            return false;
        }
    }

    return true;
}

function includeEachOther(array1, array2) {
    return includes(array1, array2) && includes(array2, array1);
}

function includesUsingEquals(array1, array2) {
    for (var i in array1) {
        if (indexOfUsingEquals(array1[i], array2) < 0) {
            return false;
        }
    }

    return true;
}

function includeEachOtherUsingEquals(array1, array2) {
    return includesUsingEquals(array1, array2) && includesUsingEquals(array2, array1);
}

function getOrCreateArray(dictionary, key) {
    var result = dictionary[key];

    if (result == undefined) {
        result = [];
        dictionary[key] = result;
    }

    return result;
}

/**
 * @return
 * <br>Array
 * <br>New
 */
function trimElements(array) {
    var result = [];

    for (var i in array) {
        result[i] = array[i].trim();
    }

    return result;
}

function isElement(element, array) {
    for (var i in array) {
        if (element == array[i]) {
            return true;
        }
    }

    return false;
}

/**
 * @param array
 * <br>Input-output
 * @return <code>true</code> iff <code>array</code> has been modified
 */
function addUnique(element, array) {
    if (!isElement(element, array)) {
        array.push(element);

        return true;
    }

    return false;
}

function isElementUsingEquals(element, array) {
    for (var i in array) {
        if (element.equals(array[i])) {
            return true;
        }
    }

    return false;
}

/**
 * @param array
 * <br>Input-output
 * @return <code>true</code> iff <code>array</code> has been modified
 */
function addUniqueUsingEquals(element, array) {
    if (!isElementUsingEquals(element, array)) {
        array.push(element);

        return true;
    }

    return false;
}

/**
 * @return
 * <br>Range: <code>[-1 .. array.length - 1]</code>
 */
function indexOfUsingEquals(element, array) {
    for (var i in array) {
        if (element.equals(array[i])) {
            return i;
        }
    }

    return -1;
}

function $element(id) {
    return document.getElementById(id);
}

function assertEquality(expected, actual) {
    if (expected != actual) {
        throw 'Assertion failed: expected ' + expected + ' but was ' + actual;
    }
}

function assertEquals(expected, actual) {
    if (!expected.equals(actual)) {
        throw 'Assertion failed: expected ' + expected + ' but was ' + actual;
    }
}

function resize(textInput, minimumSize) {
    textInput.size = Math.max(minimumSize, textInput.value.length);
}

/*
 *  The MIT License
 * 
 *  Copyright 2011 Greg.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

var EPSILON = '\'\'';

function Grammar(text) {
    // <PUBLIC>

    this.alphabet = [];
    this.nonterminals = [];
    this.terminals = [];
    this.rules = [];
    this.firsts = new Object();
    this.follows = new Object();

    this.toString = function() {
        return this.rules.join('\n');
    };

    this.getRulesForNonterminal = function(nonterminal) {
        var result = [];

        for (var i in this.rules) {
            var rule = this.rules[i];

            if (nonterminal == rule.nonterminal) {
                result.push(rule);
            }
        }

        return result;
    };

    /**
     * @param sequence
     * <br>Array of symbols
     * @result
     * <br>Array of terminal symbols
     * <br>New
     */
    this.getSequenceFirsts = function(sequence) {
        var result = [];
        var epsilonInSymbolFirsts = true;

        for (var j in sequence) {
            var symbol = sequence[j];
            epsilonInSymbolFirsts = false;

            if (isElement(symbol, this.terminals)) {
                addUnique(symbol, result);

                break;
            }

            for (var k in this.firsts[symbol]) {
                var first = this.firsts[symbol][k];

                epsilonInSymbolFirsts |= first == EPSILON;

                addUnique(first, result);
            }

            epsilonInSymbolFirsts |= this.firsts[symbol] == undefined || this.firsts[symbol].length == 0;

            if (!epsilonInSymbolFirsts) {
                break;
            }
        }

        if (epsilonInSymbolFirsts) {
            addUnique(EPSILON, result);
        }

        return result;
    };

    // </PUBLIC>

    // <INITIALIZATION>

    initializeRulesAndAlphabetAndNonterminals(this);
    initializeAlphabetAndTerminals(this);
    initializeFirsts(this);
    initializeFollows(this);

    // </INITIALIZATION>

    // <PRIVATE>

    /**
     * @param grammar
     * <br>Input-output
     */
    function initializeRulesAndAlphabetAndNonterminals(grammar) {
        var lines = text.split('\n');

        for (var i in lines) {
            var line = lines[i].trim();

            if (line != '') {
                var rule = new Rule(grammar, line);

                grammar.rules.push(rule);

                if (grammar.axiom == undefined) {
                    grammar.axiom = rule.nonterminal;
                }

                addUnique(rule.nonterminal, grammar.alphabet);
                addUnique(rule.nonterminal, grammar.nonterminals);
            }
        }
    }

    /**
     * @param grammar
     * <br>Input-output
     */
    function initializeAlphabetAndTerminals(grammar) {
        for (var i in grammar.rules) {
            var rule = grammar.rules[i];

            for (var j in rule.development) {
                var symbol = rule.development[j];

                if (symbol != EPSILON && !isElement(symbol, grammar.nonterminals)) {
                    addUnique(symbol, grammar.alphabet);
                    addUnique(symbol, grammar.terminals);
                }
            }
        }
    }

    /**
     * @param grammar
     * <br>Input-output
     */
    function initializeFirsts(grammar) {
        var notDone;

        do {
            notDone = false;

            for (var i in grammar.rules) {
                var rule = grammar.rules[i];
                var nonterminalFirsts = getOrCreateArray(grammar.firsts, rule.nonterminal);

                if (rule.development.length == 1 && rule.development[0] == EPSILON) {
                    notDone |= addUnique(EPSILON, nonterminalFirsts);
                } else {
                    notDone |= collectDevelopmentFirsts(grammar, rule.development, nonterminalFirsts);
                }
            }
        } while (notDone);
    }

    /**
     * @param grammar
     * <br>Input-output
     * @param development
     * <br>Array of symbols
     * @param nonterminalFirsts
     * <br>Array of symbols
     * <br>Input-output
     * @return <code>true</code> If <code>nonterminalFirsts</code> has been modified
     */
    function collectDevelopmentFirsts(grammar, development, nonterminalFirsts) {
        var result = false;
        var epsilonInSymbolFirsts = true;

        for (var j in development) {
            var symbol = development[j];
            epsilonInSymbolFirsts = false;

            if (isElement(symbol, grammar.terminals)) {
                result |= addUnique(symbol, nonterminalFirsts);

                break;
            }

            for (var k in grammar.firsts[symbol]) {
                var first = grammar.firsts[symbol][k];

                epsilonInSymbolFirsts |= first == EPSILON;

                result |= addUnique(first, nonterminalFirsts);
            }

            if (!epsilonInSymbolFirsts) {
                break;
            }
        }

        if (epsilonInSymbolFirsts) {
            result |= addUnique(EPSILON, nonterminalFirsts);
        }

        return result;
    }

    /**
     * @param grammar
     * <br>Input-output
     */
    function initializeFollows(grammar) {
        var notDone;

        do {
            notDone = false;

            for (var i in grammar.rules) {
                var rule = grammar.rules[i];

                if (i == 0) {
                    var nonterminalFollows = getOrCreateArray(grammar.follows, rule.nonterminal);

                    notDone |= addUnique('$', nonterminalFollows);
                }

                for (var j in rule.development) {
                    var symbol = rule.development[j];

                    if (isElement(symbol, grammar.nonterminals)) {
                        var symbolFollows = getOrCreateArray(grammar.follows, symbol);
                        var afterSymbolFirsts = grammar.getSequenceFirsts(rule.development.slice(parseInt(j) + 1));

                        for (var k in afterSymbolFirsts) {
                            var first = afterSymbolFirsts[k];

                            if (first == EPSILON) {
                                var nonterminalFollows = grammar.follows[rule.nonterminal];

                                for (var l in nonterminalFollows) {
                                    notDone |= addUnique(nonterminalFollows[l], symbolFollows);
                                }
                            } else {
                                notDone |= addUnique(first, symbolFollows);
                            }
                        }
                    }
                }
            }
        } while (notDone);
    }

    // </PRIVATE>
}

function Rule(grammar, text) {
    // <PUBLIC>

    this.grammar = grammar;
    this.index = grammar.rules.length;

    var split = text.split('->');

    this.nonterminal = split[0].trim();

    this.pattern = trimElements(this.nonterminal.split(' '));

    this.development = trimElements(split[1].trim().split(' '));

    this.toString = function() {
        return this.nonterminal + ' -> ' + this.development.join(' ');
    };

    this.equals = function(that) {
        if (this.nonterminal != that.nonterminal) {
            return false;
        }

        if (parseInt(this.development.length) != parseInt(that.development.length)) {
            return false;
        }

        for (var i in this.development) {
            if (this.development[i] != that.development[i]) {
                return false;
            }
        }

        return true;
    };

    // </PUBLIC>
}

function BasicItem(rule, dotIndex) {
    // <PUBLIC>

    this.rule = rule;

    this.dotIndex = dotIndex;

    this.lookAheads = [];

    this.addUniqueTo = function(items) {
        return addUniqueUsingEquals(this, items);
    };

    this.newItemsFromSymbolAfterDot = function() {
        var result = [];
        var nonterminalRules = this.rule.grammar.getRulesForNonterminal(this.rule.development[this.dotIndex]);

        for (var j in nonterminalRules) {
            addUniqueUsingEquals(new Item(nonterminalRules[j], 0), result);
        }

        return result;
    };

    this.newItemAfterShift = function() {
        if (this.dotIndex < this.rule.development.length && this.rule.development[this.dotIndex] != EPSILON) {
            return new Item(this.rule, this.dotIndex + 1);
        }

        return undefined;
    }

    this.equals = function(that) {
        return this.rule.equals(that.rule) && (parseInt(this.dotIndex) == parseInt(that.dotIndex));
    };

    this.toString = function() {
        return this.rule.nonterminal + ' -> ' + this.rule.development.slice(0, this.dotIndex).join(' ') + '.' +
            (isElement(EPSILON, this.rule.development) ? '' : this.rule.development.slice(this.dotIndex).join(' '));
    };

    // </PUBLIC>
}

function BasicLR1Item(rule, dotIndex) {
    // <PUBLIC>

    extend(this, new BasicItem(rule, dotIndex));

    var zuper = this.zuper;

    this.lookAheads = rule.index == 0 ? ['$'] : [];

    this.newItemsFromSymbolAfterDot = function() {
        var result = this.zuper.newItemsFromSymbolAfterDot();

        if (result.length == 0) {
            return result;
        }

        var newLookAheads = [];
        var epsilonPresent = false;
        var firstsAfterSymbolAfterDot = this.rule.grammar.getSequenceFirsts(this.rule.development.slice(this.dotIndex + 1));

        for (var i in firstsAfterSymbolAfterDot) {
            var first = firstsAfterSymbolAfterDot[i];
            if (EPSILON == first) {
                epsilonPresent = true;
            } else {
                addUnique(first, newLookAheads);
            }
        }

        if (epsilonPresent) {
            for (var i in this.lookAheads) {
                addUnique(this.lookAheads[i], newLookAheads);
            }
        }

        for (var i in result) {
            result[i].lookAheads = newLookAheads.slice(0);
        }

        return result;
    };

    this.newItemAfterShift = function() {
        var result = zuper.newItemAfterShift();

        if (result != undefined) {
            result.lookAheads = this.lookAheads.slice(0);
        }

        return result;
    }

    this.addUniqueTo = function(items) {
        var result = false;

        for (var i in items) {
            var item = items[i];

            if (zuper.equals(item)) {
                for (var i in this.lookAheads) {
                    result |= addUnique(this.lookAheads[i], item.lookAheads);
                }

                return result;
            }
        }

        items.push(this);

        return true;
    };

    this.equals = function(that) {
        return zuper.equals(that) && includeEachOther(this.lookAheads, that.lookAheads);
    }

    this.toString = function() {
        return '[' + zuper.toString() + ', ' + this.lookAheads.join('/') + ']';
    };

    // </PUBLIC>
}

/*
 *  The MIT License
 * 
 *  Copyright 2011 Greg.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

function LRTable(closureTable) {
    // <PUBLIC>

    this.grammar = closureTable.grammar;
    this.states = [];

    // </PUBLIC>

    // <INITIALIZATION>

    for (var i in closureTable.kernels) {
        var kernel = closureTable.kernels[i];
        var state = new State(this.states);

        for (var j in kernel.keys) {
            var key = kernel.keys[j];
            var nextStateIndex = kernel.gotos[key];

            getOrCreateArray(state, key).push(new LRAction((isElement(key, closureTable.grammar.terminals) ? 's' : ''), nextStateIndex));
        }

        for (var j in kernel.closure) {
            var item = kernel.closure[j];

            if (item.dotIndex == item.rule.development.length || item.rule.development[0] == EPSILON) {
                for (var k in item.lookAheads) {
                    var lookAhead = item.lookAheads[k];

                    getOrCreateArray(state, lookAhead).push(new LRAction('r', item.rule.index));
                }
            }
        }
    }

    // </INITIALIZATION>
}

/**
 * @param states
 * <br>Input-output
 */
function State(states) {
    // <PUBLIC>

    this.index = states.length;

    // </PUBLIC>

    // <INITIALIZATION>

    states.push(this);

    // <//INITIALIZATION>
}

function LRAction(actionType, actionValue) {
    // <PUBLIC>

    this.actionType = actionType;
    this.actionValue = actionValue;

    this.toString = function() {
        return this.actionType + this.actionValue;
    };

    // </PUBLIC>
}

function chooseActionElement(state, token) {
    var action = state[token];

    if (action == undefined) {
        return undefined;
    }

    var radios = document.getElementsByName(state.index + "_" + token);

    for (var i = 0; i < radios.length; ++i) {
        if (radios[i].checked) {
            return action[i];
        }
    }

    return action[0];
}

/*
 *  The MIT License
 * 
 *  Copyright 2011 Greg.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

function LRClosureTable(grammar) {
    // <PUBLIC>

    this.grammar = grammar;
    this.kernels = [];

    // </PUBLIC>

    // <INITIALIZATION>

    this.kernels.push(new Kernel(0, [new Item(grammar.rules[0], 0)], grammar));

    for (var i = 0; i < this.kernels.length;) {
        var kernel = this.kernels[i];

        updateClosure(kernel);

        if (addGotos(kernel, this.kernels)) {
            i = 0;
        } else {
            ++i;
        }
    }

    // </INITIALIZATION>

    // <PRIVATE>

    /**
     * @param kernel
     * <br>Input-output
     */
    function updateClosure(kernel) {
        for (var i = 0; i < kernel.closure.length; ++i) {
            var newItemsFromSymbolAfterDot = kernel.closure[i].newItemsFromSymbolAfterDot();

            for (var j in newItemsFromSymbolAfterDot) {
                newItemsFromSymbolAfterDot[j].addUniqueTo(kernel.closure);
            }
        }
    }

    /**
     * @param kernel
     * <br>Input-output
     * @param kernels
     * <br>Input-output
     */
    function addGotos(kernel, kernels) {
        var lookAheadsPropagated = false;
        var newKernels = new Object();

        for (var i in kernel.closure) {
            var item = kernel.closure[i];
            var newItem = item.newItemAfterShift();

            if (newItem != undefined) {
                var symbolAfterDot = item.rule.development[item.dotIndex];

                addUnique(symbolAfterDot, kernel.keys);
                newItem.addUniqueTo(getOrCreateArray(newKernels, symbolAfterDot));
            }
        }

        for (var i in kernel.keys) {
            var key = kernel.keys[i];
            var newKernel = new Kernel(kernels.length, newKernels[key], grammar);
            var targetKernelIndex = indexOfUsingEquals(newKernel, kernels);

            if (targetKernelIndex < 0) {
                kernels.push(newKernel);
                targetKernelIndex = newKernel.index;
            } else {
                for (var j in newKernel.items) {
                    lookAheadsPropagated |= newKernel.items[j].addUniqueTo(kernels[targetKernelIndex].items);
                }
            }

            kernel.gotos[key] = targetKernelIndex;
        }

        return lookAheadsPropagated;
    }

    // </PRIVATE>
}

function Kernel(index, items, grammar) {
    // <PUBLIC>

    this.index = index;
    this.items = items;
    this.closure = this.items.slice(0);
    this.gotos = new Object();
    this.keys = [];

    this.equals = function(that) {
        return includeEachOtherUsingEquals(this.items, that.items);
    };

    this.toString = function() {
        return 'closure{' + this.items + '} = {' + this.closure + '}';
    };

    // </PUBLIC>
}

function Item(rule, dotIndex) {
    // <PUBLIC>

    extend(this, new BasicItem(rule, dotIndex));

    this.lookAheads = rule.grammar.follows[rule.nonterminal];

    // </PUBLIC>
}

Item.prototype.grammarType = 'SLR';

function Tree(value, children) {
    this.value = value;
    this.children = children;

    this.toString = function() {
        return this.value.toString();
    }
}


function getCurrentStackValue(stack) {
    var result = stack.slice(0);

    for (var i = 0; i < result.length; i += 2) {
        result[i] = "<strong>" + result[i] + "</strong>";
    }

    return result.join(' ');
}

function getCurrentTransitionValue(state, token) {
    var action = state[token];

    if (action == undefined) {
        return "&nbsp;";
    }

    var formattedActionElements = [];

    
    formattedActionElements.push(getCurrentTransitionValueElement(chooseActionElement(state, token)));
    

    var result = formattedActionElements.join(" / ");

    if (1 < action.length) {
        result = "<span style=\"background-color: pink;\">" + result + "</span>";
    }

    return result;
}

function getCurrentTransitionValueElement(actionElement) {
    return actionElement.toString()
        .replace("r0", "<span>OK</span>")
        .replace(/(s|\b)([0-9]+)/g, "$1<span>$2</span>")
        .replace(/r([0-9]+)/g, "r<sub>$1</sub>")
        .replace("s", "");
}
