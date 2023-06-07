import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";


export default class IndexPage extends BaseClass {

    constructor() {
        super();
        this.bindClassMethods([], this);
        this.dataStore = new DataStore();
    }

    async mount() {

        // this.dataStore.addChangeListener();
    }


}


const main = async () => {
    const indexPage = new IndexPage();
    await indexPage.mount();
};

window.addEventListener('DOMContentLoaded', main);