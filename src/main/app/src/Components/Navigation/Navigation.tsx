import React, {useContext} from 'react';
import {
    Box,
    createStyles,
    Menu,
    MenuItem,
    MenuProps,
    Theme,
    Typography,
    withStyles,
} from '@material-ui/core';
import {Link} from 'react-router-dom';

import Toolbar from '@material-ui/core/Toolbar';

import makeStyles from '@material-ui/core/styles/makeStyles';
import IconButton from '@material-ui/core/IconButton';
import Avatar from '@material-ui/core/Avatar';
import {withBasePath} from '../../utils/API/BasePath';
import Notification from '../Notification/Notification';
import componentStyles from '../../Resources/styling/ComponentStyles';
import LandscapeWatcher from '../Landscape/Dashboard/LandscapeWatcher';
import {LandscapeContext} from '../../Context/LandscapeContext';
import SearchField from '../Landscape/Search/SearchField';

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        pageTitle: {
            padding: 11,
            paddingLeft: 16,
            paddingRight: 16,
            flexGrow: 1,
        },
        logo: {
            height: '1.5em',
            width: '1.5em',
        },
        appBar: {
            position: 'relative',
            backgroundColor: theme.palette.primary.main,
        },
    })
);

interface Props {
    setSidebarContent: Function;
    pageTitle?: string;
    logo?: string;
    version?: string;
}

var requestURL = "/user";
var request = new XMLHttpRequest();
request.open("GET", requestURL, true);
request.responseType = 'json';
request.send();
console.log("TEST: + " + request.response);

request.onload = function() {
    let data = request.response;

    let user = document.getElementById("user");
    if( user){
      console.log("user: + " + data);
      // user.innerHTML(data.name);
    }
    let userUnauthenticated = document.getElementById(".containerUnauthenticated");
    if (userUnauthenticated){
        userUnauthenticated.style.display = 'none';
    }
    let userAuthenticated = document.getElementById(".containerAuthenticated");
    if (userAuthenticated) {
        userAuthenticated.style.display = 'block';
    }
};




/**
 * Header Component
 */
const Navigation: React.FC<Props> = ({setSidebarContent, pageTitle, logo, version}) => {
    const classes = useStyles();
    const componentClasses = componentStyles();
    const landscapeContext = useContext(LandscapeContext);
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

    const openMenu = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const StyledMenu = withStyles((theme: Theme) =>
        createStyles({
            paper: {
                backgroundColor: theme.palette.primary.main,
                marginTop: 5,
            },
        })
    )((props: MenuProps) => (
        <Menu
            elevation={0}
            getContentAnchorEl={null}
            anchorOrigin={{vertical: 'bottom', horizontal: 'center'}}
            transformOrigin={{vertical: 'top', horizontal: 'center'}}
            {...props}
        />
    ));
    return (
        <Toolbar className={classes.appBar} variant={'dense'}>
            <IconButton
                size={'small'}
                edge='start'
                color='inherit'
                aria-controls='simple-menu'
                aria-haspopup='true'
                onClick={openMenu}
                className={componentClasses.navigationButton}
            >
                {logo ? (
                    <Avatar
                        className={classes.logo}
                        imgProps={{style: {objectFit: 'contain'}}}
                        src={logo}
                        alt={'logo'}
                    />
                ) : (
                    <Avatar
                        className={classes.logo}
                        imgProps={{style: {objectFit: 'contain'}}}
                        src={withBasePath('icons/svg/nivio.svg')}
                        alt={'logo'}
                    />
                )}
            </IconButton>
            <StyledMenu anchorEl={anchorEl} keepMounted open={Boolean(anchorEl)} onClose={handleClose}>
                <MenuItem component={Link} to={``} onClick={handleClose}>
                    Home
                </MenuItem>
                <MenuItem component={Link} to={`/man/install.html`} onClick={handleClose}>
                    Help
                </MenuItem>
                <MenuItem disabled={true}>
                    <Typography style={{fontSize: 10}}>nivio {version}</Typography>
                </MenuItem>
            </StyledMenu>
            <Box className={classes.pageTitle}>
                <Typography variant='h6'>{pageTitle}</Typography>
            </Box>
            <div className="containerUnauthenticated">
                With GitHub: <a href="/oauth2/authorization/github">click here</a>
            </div>
            <div className="containerAuthenticated" style={{display: 'none'}}>
                Logged in as: <span id="user"></span>
            </div>
            {/*<script type="text/javascript">*/}
            {/*    $.get("/user", function(data) {*/}
            {/*    $("#user").html(data.name);*/}
            {/*    $(".unauthenticated").hide()*/}
            {/*    $(".authenticated").show()*/}
            {/*});*/}
            {/*</script>*/}

            {landscapeContext.identifier ? <Notification setSidebarContent={setSidebarContent}/> : null}
            <LandscapeWatcher setSidebarContent={setSidebarContent}/>
            {landscapeContext.identifier ? (
                <SearchField setSidebarContent={setSidebarContent}/>
            ) : null}{' '}
        </Toolbar>
    );
};

export default Navigation;
