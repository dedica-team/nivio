import API from './APIConfig';
import Axios from 'axios';

export const get = async (route: string) => {
  let data = null;

  const response = await API.get(route);
  data = response.data;

  return data;
};

export const getJsonFromUrl = async (url: string) => {
  let data = null;
  const response = await Axios({
    method: 'get',
    url,
    responseType: 'json',
  });

  data = response.data;

  return data;
};
