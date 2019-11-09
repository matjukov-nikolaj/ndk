'use strict';
let allTree = new Tree(globalConfig.MAIN_NAME);
const saveModalWindow = new SaveModal(allTree);
const loadModalWindow = new LoadModal(allTree);
const InformationWindow = new HelpModal();
const treeRenderer = new TreeRenderer(allTree);
const treeController = new TreeController(allTree, treeRenderer);
loadModalWindow.onLoadTree = (tree) => {
  treeController.setTree(tree);
  allTree = tree;
};
treeRenderer.drawAllTree(treeController.selection);