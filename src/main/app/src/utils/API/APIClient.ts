import API from './APIConfig';
import Axios, { CancelTokenSource } from 'axios';

export const get = async (route: string, cancelTokenSource?: CancelTokenSource) => {
  let data = null;

  const response = await API.get(route, {
    cancelToken: cancelTokenSource?.token,
  });
  data = response.data;

  return data;
};

export const getJsonFromUrl = async (url: string, cancelTokenSource?: CancelTokenSource) => {
  let data = null;
  const response = await Axios({
    method: 'get',
    url,
    responseType: 'json',
    cancelToken: cancelTokenSource?.token,
  });

  data = response.data;

  return data;
};
