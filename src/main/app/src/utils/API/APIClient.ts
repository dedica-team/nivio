import API from './APIConfig';
import Axios from 'axios';

export const get = async (route: string) => {
  let data = null;

  await API.get(route).then((res) => {
    data = res.data;
  });

  return data;
};

export const getJsonFromUrl = async (url: string) => {
  let data = null;
  await Axios({
    method: 'get',
    url,
    responseType: 'json',
  }).then((res) => {
    data = res.data;
  });

  return data;
};
