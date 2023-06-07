import BaseClass from "../util/baseClass";
import axios from "axios";

const PROFILE_SERVICE_BASE_PATH = "/api/v1/profileService"
const GET_PROFILE_ENDPOINT = (userId) => `/profile/${userId}`;
const CREATE_PROFILE_ENDPOINT = "/profile";
const UPDATE_PROFILE_ENDPOINT = "/profile";
const DELETE_PROFILE_ENDPOINT = (userId) => `/profile/${userId}`;

export default class ProfileServiceClient extends BaseClass {

    constructor(props = {}) {
        super();
        const methodsToBind = []
        this.bindClassMethods(methodsToBind);
        this.props = props;
        this.clientLoaded(axios);
    }

    /**
     * Run any functions that are supposed to be called once the client has loaded successfully.
     */
    clientLoaded(client) {
        this.client = client;
        if (this.props.hasOwnProperty("onReady")){
            this.props.onReady();
        }
    }

    async getProfileByUserId(userId, errorCallback) {
        try {
            const response = await this.client.get(`${PROFILE_SERVICE_BASE_PATH}${GET_PROFILE_ENDPOINT(userId)}`);
            return response.data;
        } catch (error) {
            this.handleError("getProfileByUserId", error, errorCallback);
        }
    }

    async createProfile(userId, name, email, diets, errorCallback) {
        try {
            const response = this.client.post(`${PROFILE_SERVICE_BASE_PATH}${CREATE_PROFILE_ENDPOINT}`, {
                userId: userId,
                name: name,
                email: email,
                diets: diets
            });
            return response.data;
        } catch (error) {
            this.handleError("createProfile", error, errorCallback);
        }
    }

    async updateProfile(userId, name, email, diets, errorCallback) {
        try {
            const response = await this.client.put(`${PROFILE_SERVICE_BASE_PATH}${UPDATE_PROFILE_ENDPOINT}`, {
                userId: userId,
                name: name,
                email: email,
                diets: diets
            });
            return response.data;
        } catch (error) {
            this.handleError("updateProfile", error, errorCallback);
        }
    }
    async deleteProfile(userId, errorCallback) {
        try {
            const response = this.client.delete(`${PROFILE_SERVICE_BASE_PATH}${DELETE_PROFILE_ENDPOINT(userId)}`);
            return response.data;
        } catch (error) {
            this.handleError("deleteProfile", error, errorCallback);
        }
    }


    /**
     * Helper method to log the error and run any error functions.
     * @param error The error received from the server.
     * @param errorCallback (Optional) A function to execute if the call fails.
     */
    handleError(method, error, errorCallback) {
        console.error(method + " failed - " + error);
        if (error.response.data.message !== undefined) {
            console.error(error.response.data.message);
        }
        if (errorCallback) {
            errorCallback(method + " failed - " + error);
        }
    }
}