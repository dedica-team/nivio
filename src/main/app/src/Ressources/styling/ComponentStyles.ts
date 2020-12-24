import {Theme} from '@material-ui/core';
import {createStyles, makeStyles} from "@material-ui/core/styles";

const componentStyles = makeStyles((theme: Theme) =>
    createStyles({
        card: {
            margin: 5,
            marginTop: 0,
            padding: 5,
            backgroundColor: theme.palette.secondary.main,
        },
        icon: {
            height: '2em',
        },
        floatingButton: {
            float: 'right',
        },
    })
);

export default componentStyles;